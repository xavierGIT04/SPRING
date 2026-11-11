package tg.nassara.estimation.service.estimation;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import tg.nassara.estimation.entity.AnnonceMarche;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.enums.MethodeEstimation;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.StatutTransaction;
import tg.nassara.estimation.enums.TypeAnnonce;
import tg.nassara.estimation.exception.DonneesInsuffisantesException;
import tg.nassara.estimation.repository.AnnonceMarcheRepository;
import tg.nassara.estimation.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Méthode 2 : comparaison de marché.
 *
 * <p>Retient 2 à 10 annonces de VENTE comparables (même quartier, même nature, surface de terrain proche,
 * récentes), calcule le prix demandé au m² (médiane, quartiles), le corrige du biais « prix demandé / prix réel »
 * puis l'applique à la surface du bien. Sous le minimum de comparables : {@link DonneesInsuffisantesException}
 * (méthode écartée, l'estimation continue avec les autres).
 *
 * <p>Les annonces sont lues en base : l'actualisation du catalogue est faite en amont par
 * {@link tg.nassara.estimation.service.marche.ServiceSynchronisationCatalogue}.
 *
 * <p>Hypothèse : la comparaison porte sur le prix global rapporté à la SURFACE DU TERRAIN, pour les deux natures.
 */
@Component
public class EstimateurComparaisonMarche implements Estimateur {

    private static final ZoneId FUSEAU = ZoneId.of("Africa/Lome");
    private static final BigDecimal QUARTILE_BAS = new BigDecimal("0.25");
    private static final BigDecimal QUARTILE_HAUT = new BigDecimal("0.75");

    private final ParametresCalcul parametres;
    private final AnnonceMarcheRepository annonceRepository;
    private final TransactionRepository transactionRepository;

    public EstimateurComparaisonMarche(ParametresCalcul parametres, AnnonceMarcheRepository annonceRepository,
                                       TransactionRepository transactionRepository) {
        this.parametres = parametres;
        this.annonceRepository = annonceRepository;
        this.transactionRepository = transactionRepository;
    }

    private record Retenu(AnnonceMarche annonce, BigDecimal prixM2, BigDecimal ecartSurface) {
    }

    private record Correction(BigDecimal facteur, String source, int nombreTransactions,
                              BigDecimal medianeTransactionsM2) {
    }

    @Override
    public MethodeEstimation methode() {
        return MethodeEstimation.COMPARAISON_MARCHE;
    }

    @Override
    public boolean estApplicable(Bien bien) {
        return bien != null;
    }

    @Override
    public ResultatMethodeCalcule estimer(Bien bien) {
        Bien reel = (Bien) Hibernate.unproxy(bien);
        BigDecimal surface = reel.getSurfaceTerrainM2();
        if (surface == null || surface.signum() <= 0) {
            throw new DonneesInsuffisantesException(methode(), "surface du terrain absente ou nulle.");
        }
        Quartier quartier = reel.getQuartier();
        NatureBien nature = reel.nature();
        List<String> avertissements = new ArrayList<>();
        Map<String, Object> detail = new LinkedHashMap<>();

        int fenetreJours = parametres.entier(ClesParametres.MARCHE_FENETRE_JOURS);
        int minimum = parametres.entier(ClesParametres.MARCHE_MIN_COMPARABLES);
        int maximum = parametres.entier(ClesParametres.MARCHE_MAX_COMPARABLES);
        BigDecimal tolerance = fraction(ClesParametres.MARCHE_TOLERANCE_SURFACE);
        if (fenetreJours <= 0 || minimum < 1 || maximum < minimum) {
            throw new IllegalStateException("Paramétrage incohérent : il faut " + ClesParametres.MARCHE_FENETRE_JOURS
                    + " > 0 et 1 <= " + ClesParametres.MARCHE_MIN_COMPARABLES + " <= "
                    + ClesParametres.MARCHE_MAX_COMPARABLES + " (" + fenetreJours + " / " + minimum + " / "
                    + maximum + ").");
        }

        List<Retenu> retenus = rechercherComparables(quartier, nature, surface, tolerance, fenetreJours, maximum);
        if (retenus.size() < minimum) {
            throw new DonneesInsuffisantesException(methode(),
                    "la recherche de biens comparables n'a pas été concluante : " + retenus.size()
                            + " bien(s) comparable(s) trouvé(s) dans le quartier « " + quartier.getLibelle()
                            + " » pour une surface proche de " + surface.stripTrailingZeros().toPlainString()
                            + " m² (minimum requis : " + minimum + ").");
        }

        List<BigDecimal> prixM2 = retenus.stream().map(Retenu::prixM2).toList();
        BigDecimal mediane = Statistiques.mediane(prixM2);
        BigDecimal bas = Statistiques.quantile(prixM2, QUARTILE_BAS);
        BigDecimal haut = Statistiques.quantile(prixM2, QUARTILE_HAUT);
        BigDecimal dispersion = Statistiques.coefficientVariation(prixM2);

        Correction correction = determinerCorrection(quartier, nature, mediane, avertissements);

        BigDecimal prixCentral = ResultatMethodeCalcule.arrondiXof(
                mediane.multiply(correction.facteur()).multiply(surface));
        BigDecimal prixMin = ResultatMethodeCalcule.arrondiXof(bas.multiply(correction.facteur()).multiply(surface));
        BigDecimal prixMax = ResultatMethodeCalcule.arrondiXof(haut.multiply(correction.facteur()).multiply(surface));
        BigDecimal confiance = calculerConfiance(retenus.size(), maximum, dispersion,
                "DECOTE_PAR_DEFAUT".equals(correction.source()));

        if (retenus.size() == minimum) {
            avertissements.add("Nombre de comparables au minimum requis (" + minimum + ") : fourchette peu robuste.");
        }
        if (nature == NatureBien.BATI) {
            avertissements.add("Bien bâti : comparaison sur le prix global rapporté à la surface du terrain, "
                    + "sans distinction de standing ni de surface bâtie.");
        }

        detail.put("quartier", quartier.getCode());
        detail.put("surfaceTerrainM2", surface);
        detail.put("fenetreJours", fenetreJours);
        detail.put("toleranceSurface", tolerance);
        detail.put("nombreComparablesRetenus", retenus.size());
        detail.put("comparables", lignesComparables(retenus));
        detail.put("prixM2Mediane", mediane);
        detail.put("prixM2Q1", bas);
        detail.put("prixM2Q3", haut);
        detail.put("coefficientVariation", dispersion);
        Map<String, Object> detailCorrection = new LinkedHashMap<>();
        detailCorrection.put("facteur", correction.facteur());
        detailCorrection.put("source", correction.source());
        detailCorrection.put("nombreTransactions", correction.nombreTransactions());
        detailCorrection.put("medianeTransactionsM2", correction.medianeTransactionsM2());
        detail.put("correction", detailCorrection);
        detail.put("prixM2CentralCorrige", mediane.multiply(correction.facteur()));
        detail.put("indiceConfiance", confiance);

        return new ResultatMethodeCalcule(methode(), prixMin, prixCentral, prixMax, confiance, detail, avertissements);
    }

    // ---------- Recherche des comparables ----------

    private List<Retenu> rechercherComparables(Quartier quartier, NatureBien nature, BigDecimal surface,
                                               BigDecimal tolerance, int fenetreJours, int maximum) {
        BigDecimal surfaceMin = surface.multiply(BigDecimal.ONE.subtract(tolerance));
        BigDecimal surfaceMax = surface.multiply(BigDecimal.ONE.add(tolerance));
        LocalDate depuis = LocalDate.now(FUSEAU).minusDays(fenetreJours);

        List<AnnonceMarche> trouvees = annonceRepository.findComparables(
                quartier.getId(), nature, TypeAnnonce.VENTE, depuis, surfaceMin, surfaceMax);

        return trouvees.stream()
                .filter(a -> a.getMontant() != null && a.getMontant().signum() > 0
                        && a.getSurfaceTerrainM2() != null && a.getSurfaceTerrainM2().signum() > 0)
                .map(a -> new Retenu(a,
                        a.getMontant().divide(a.getSurfaceTerrainM2(), 2, RoundingMode.HALF_UP),
                        a.getSurfaceTerrainM2().subtract(surface).abs()))
                .sorted(Comparator.comparing(Retenu::ecartSurface)
                        .thenComparing(r -> r.annonce().getDateCollecte(), Comparator.<LocalDate>reverseOrder()))
                .limit(maximum)
                .toList();
    }

    private List<Map<String, Object>> lignesComparables(List<Retenu> retenus) {
        List<Map<String, Object>> lignes = new ArrayList<>();
        for (Retenu r : retenus) {
            AnnonceMarche a = r.annonce();
            Map<String, Object> ligne = new LinkedHashMap<>();
            ligne.put("id", a.getId());
            ligne.put("origine", a.getOrigine() != null ? a.getOrigine().name() : null);
            ligne.put("source", a.getSource());
            ligne.put("urlSource", a.getUrlSource());
            ligne.put("titre", a.getTitre());
            ligne.put("montant", a.getMontant());
            ligne.put("surfaceTerrainM2", a.getSurfaceTerrainM2());
            ligne.put("prixM2", r.prixM2());
            ligne.put("dateCollecte", a.getDateCollecte() != null ? a.getDateCollecte().toString() : null);
            lignes.add(ligne);
        }
        return lignes;
    }

    // ---------- Correction du biais « prix demandé / prix réel » ----------

    /**
     * Avec assez de transactions validées dans le quartier : facteur = médiane des prix réels au m² / médiane des
     * prix demandés, borné. Sinon : décote de négociation par défaut (paramètre), signalée en avertissement.
     */
    private Correction determinerCorrection(Quartier quartier, NatureBien nature, BigDecimal medianeAnnonces,
                                            List<String> avertissements) {
        int minTransactions = parametres.entier(ClesParametres.MARCHE_MIN_TRANSACTIONS_CORRECTION);
        BigDecimal plancher = parametres.valeur(ClesParametres.MARCHE_CORRECTION_MIN);
        BigDecimal plafond = parametres.valeur(ClesParametres.MARCHE_CORRECTION_MAX);
        if (minTransactions < 1 || plancher.signum() <= 0 || plancher.compareTo(plafond) > 0) {
            throw new IllegalStateException("Paramétrage incohérent : il faut "
                    + ClesParametres.MARCHE_MIN_TRANSACTIONS_CORRECTION + " >= 1 et 0 < "
                    + ClesParametres.MARCHE_CORRECTION_MIN + " <= " + ClesParametres.MARCHE_CORRECTION_MAX + ".");
        }

        List<BigDecimal> prixTransactions = transactionRepository
                .findHistoriqueParQuartier(quartier.getId(), StatutTransaction.VALIDEE).stream()
                .filter(t -> t.getBien().nature() == nature)
                .filter(t -> t.getPrixVenteEffectif() != null && t.getPrixVenteEffectif().signum() > 0
                        && t.getBien().getSurfaceTerrainM2() != null
                        && t.getBien().getSurfaceTerrainM2().signum() > 0)
                .map(t -> t.getPrixVenteEffectif().divide(t.getBien().getSurfaceTerrainM2(), 2, RoundingMode.HALF_UP))
                .toList();

        if (prixTransactions.size() >= minTransactions) {
            BigDecimal medianeTransactions = Statistiques.mediane(prixTransactions);
            BigDecimal ratio = medianeTransactions.divide(medianeAnnonces, 4, RoundingMode.HALF_UP);
            BigDecimal facteur = ratio.max(plancher).min(plafond);
            if (facteur.compareTo(ratio) != 0) {
                avertissements.add("Facteur de correction borné : calculé " + ratio + ", retenu " + facteur
                        + " (bornes " + plancher + " à " + plafond + ").");
            }
            return new Correction(facteur, "TRANSACTIONS_VALIDEES", prixTransactions.size(), medianeTransactions);
        }

        BigDecimal decote = parametres.valeur(ClesParametres.MARCHE_DECOTE_NEGOCIATION_DEFAUT);
        if (decote.signum() <= 0 || decote.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("Paramétrage incohérent : "
                    + ClesParametres.MARCHE_DECOTE_NEGOCIATION_DEFAUT + " doit être compris entre 0 (exclu) et 1 ("
                    + decote + ").");
        }
        avertissements.add("Correction non calibrée sur des transactions (" + prixTransactions.size() + " validée(s) "
                + "dans le quartier, " + minTransactions + " requises) : décote de négociation par défaut de "
                + BigDecimal.ONE.subtract(decote).multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
                + " % appliquée aux prix demandés.");
        return new Correction(decote, "DECOTE_PAR_DEFAUT", prixTransactions.size(), null);
    }

    // ---------- Confiance ----------

    /**
     * Confiance = min + (max − min) × volume × (1 − pénalité × dispersion), où volume = comparables / maximum.
     * Multipliée par un facteur si la correction est la décote par défaut (non calibrée).
     */
    private BigDecimal calculerConfiance(int nombre, int maximum, BigDecimal dispersion, boolean decoteParDefaut) {
        BigDecimal min = fraction(ClesParametres.MARCHE_CONFIANCE_MIN);
        BigDecimal max = fraction(ClesParametres.MARCHE_CONFIANCE_MAX);
        BigDecimal facteurDecote = fraction(ClesParametres.MARCHE_CONFIANCE_FACTEUR_DECOTE_DEFAUT);
        BigDecimal penalite = parametres.valeur(ClesParametres.MARCHE_PENALITE_DISPERSION);
        if (min.compareTo(max) > 0 || penalite.signum() < 0) {
            throw new IllegalStateException("Paramétrage incohérent : il faut " + ClesParametres.MARCHE_CONFIANCE_MIN
                    + " <= " + ClesParametres.MARCHE_CONFIANCE_MAX + " et " + ClesParametres.MARCHE_PENALITE_DISPERSION
                    + " >= 0.");
        }
        BigDecimal volume = BigDecimal.valueOf(nombre)
                .divide(BigDecimal.valueOf(maximum), 4, RoundingMode.HALF_UP).min(BigDecimal.ONE);
        BigDecimal facteurDispersion = BigDecimal.ONE.subtract(penalite.multiply(dispersion)).max(BigDecimal.ZERO);
        BigDecimal confiance = min.add(max.subtract(min).multiply(volume).multiply(facteurDispersion));
        if (decoteParDefaut) {
            confiance = confiance.multiply(facteurDecote);
        }
        return confiance.max(BigDecimal.ZERO).min(BigDecimal.ONE).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal fraction(String cle) {
        BigDecimal valeur = parametres.valeur(cle);
        if (valeur.signum() < 0 || valeur.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("Paramétrage incohérent : " + cle
                    + " doit être compris entre 0 et 1 (" + valeur + ").");
        }
        return valeur;
    }
}
