package tg.nassara.estimation.service.estimation;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import tg.nassara.estimation.entity.AnnonceMarche;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.entity.BienBati;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.enums.MethodeEstimation;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.PeriodiciteLoyer;
import tg.nassara.estimation.enums.TypeAnnonce;
import tg.nassara.estimation.exception.DonneesInsuffisantesException;
import tg.nassara.estimation.repository.AnnonceMarcheRepository;

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
 * Méthode 3 : capitalisation du revenu locatif (biens bâtis locatifs ou commerciaux).
 *
 * <p>Loyer mensuel de référence = médiane des loyers comparables du quartier (catalogue et saisies des agents),
 * ramenés à un équivalent mensuel non meublé : un tarif à la nuitée est multiplié par le nombre de nuits payées par
 * mois (0 = nuitées ignorées), un loyer meublé par un facteur. Valeur = loyer × 12 × (1 − charges) ÷ taux de
 * capitalisation (taux du quartier, sinon taux par défaut). Fourchette = premier et troisième quartiles des loyers.
 *
 * <p>Sous le minimum de loyers comparables, le loyer actuel du bien (saisi par l'agent) sert de base, avec une marge
 * et une confiance réduite ; sans lui, {@link DonneesInsuffisantesException} (méthode écartée). Quand les deux
 * existent, le marché est prioritaire et le loyer du bien sert de recoupement.
 *
 * <p>Hypothèse : tous les loyers d'un quartier sont comparables entre eux (pas de distinction de type de bien,
 * de surface bâtie ni de standing).
 */
@Component
public class EstimateurCapitalisation implements Estimateur {

    private static final ZoneId FUSEAU = ZoneId.of("Africa/Lome");
    private static final BigDecimal QUARTILE_BAS = new BigDecimal("0.25");
    private static final BigDecimal QUARTILE_HAUT = new BigDecimal("0.75");
    private static final BigDecimal MOIS_PAR_AN = BigDecimal.valueOf(12);
    private static final BigDecimal NUITS_MAX_PAR_MOIS = BigDecimal.valueOf(31);

    private final ParametresCalcul parametres;
    private final AnnonceMarcheRepository annonceRepository;

    public EstimateurCapitalisation(ParametresCalcul parametres, AnnonceMarcheRepository annonceRepository) {
        this.parametres = parametres;
        this.annonceRepository = annonceRepository;
    }

    private record Retenu(AnnonceMarche annonce, BigDecimal loyerMensuel,
                          boolean convertiDepuisNuitee, boolean ajusteMeuble) {
    }

    private record Selection(List<Retenu> retenus, int nuiteesIgnorees) {
    }

    @Override
    public MethodeEstimation methode() {
        return MethodeEstimation.CAPITALISATION;
    }

    /** Bien bâti déclaré locatif ou commercial. */
    @Override
    public boolean estApplicable(Bien bien) {
        if (bien == null) {
            return false;
        }
        Bien reel = (Bien) Hibernate.unproxy(bien);
        return reel instanceof BienBati bati && bati.isLocatifOuCommercial();
    }

    @Override
    public ResultatMethodeCalcule estimer(Bien bien) {
        Bien reel = (Bien) Hibernate.unproxy(bien);
        if (!(reel instanceof BienBati bati)) {
            throw new DonneesInsuffisantesException(methode(), "la capitalisation ne concerne que les biens bâtis.");
        }
        if (!bati.isLocatifOuCommercial()) {
            throw new DonneesInsuffisantesException(methode(), "le bien n'est pas déclaré locatif ou commercial.");
        }
        Quartier quartier = bati.getQuartier();
        List<String> avertissements = new ArrayList<>();
        Map<String, Object> detail = new LinkedHashMap<>();

        // ----- Paramètres (un paramètre manquant arrête l'estimation) -----
        BigDecimal tauxCharges = parametres.valeur(ClesParametres.CAPITALISATION_TAUX_CHARGES);
        BigDecimal nuitsParMois = parametres.valeur(ClesParametres.CAPITALISATION_NUITEES_PAR_MOIS);
        BigDecimal facteurMeuble = parametres.valeur(ClesParametres.CAPITALISATION_FACTEUR_MEUBLE);
        int fenetreJours = parametres.entier(ClesParametres.MARCHE_FENETRE_JOURS);
        int minimum = parametres.entier(ClesParametres.CAPITALISATION_MIN_COMPARABLES);
        int maximum = parametres.entier(ClesParametres.MARCHE_MAX_COMPARABLES);
        if (tauxCharges.signum() < 0 || tauxCharges.compareTo(BigDecimal.ONE) >= 0) {
            throw new IllegalStateException("Paramétrage incohérent : " + ClesParametres.CAPITALISATION_TAUX_CHARGES
                    + " doit être compris entre 0 (inclus) et 1 (exclu) (" + tauxCharges + ").");
        }
        if (nuitsParMois.signum() < 0 || nuitsParMois.compareTo(NUITS_MAX_PAR_MOIS) > 0) {
            throw new IllegalStateException("Paramétrage incohérent : " + ClesParametres.CAPITALISATION_NUITEES_PAR_MOIS
                    + " doit être compris entre 0 et 31 (" + nuitsParMois + ").");
        }
        if (facteurMeuble.signum() <= 0 || facteurMeuble.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("Paramétrage incohérent : " + ClesParametres.CAPITALISATION_FACTEUR_MEUBLE
                    + " doit être compris entre 0 (exclu) et 1 (" + facteurMeuble + ").");
        }
        if (fenetreJours <= 0 || minimum < 1 || maximum < minimum) {
            throw new IllegalStateException("Paramétrage incohérent : il faut " + ClesParametres.MARCHE_FENETRE_JOURS
                    + " > 0 et 1 <= " + ClesParametres.CAPITALISATION_MIN_COMPARABLES + " <= "
                    + ClesParametres.MARCHE_MAX_COMPARABLES + " (" + fenetreJours + " / " + minimum + " / "
                    + maximum + ").");
        }

        // ----- Taux de capitalisation -----
        BigDecimal tauxQuartier = quartier.getTauxCapitalisation();
        boolean tauxLocal = tauxQuartier != null;
        BigDecimal taux = tauxLocal ? tauxQuartier : parametres.valeur(ClesParametres.CAPITALISATION_TAUX_DEFAUT);
        if (taux.signum() <= 0 || taux.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("Paramétrage incohérent : le taux de capitalisation "
                    + (tauxLocal ? "du quartier « " + quartier.getLibelle() + " »" : ClesParametres.CAPITALISATION_TAUX_DEFAUT)
                    + " doit être compris entre 0 (exclu) et 1 (" + taux + ").");
        }
        if (!tauxLocal) {
            avertissements.add("Taux de capitalisation par défaut (" + pourcentage(taux) + ") appliqué : le quartier « "
                    + quartier.getLibelle() + " » n'a pas de rendement propre.");
        }

        // ----- Loyers -----
        Selection selection = rechercherLoyers(quartier, fenetreJours, maximum, nuitsParMois, facteurMeuble);
        List<Retenu> retenus = selection.retenus();
        BigDecimal loyerBien = bati.getLoyerMensuelActuel();
        boolean loyerBienUtilisable = loyerBien != null && loyerBien.signum() > 0;

        BigDecimal loyerCentral;
        BigDecimal loyerBas;
        BigDecimal loyerHaut;
        BigDecimal dispersion = BigDecimal.ZERO;
        int nombre;
        String sourceLoyer;

        if (retenus.size() >= minimum) {
            List<BigDecimal> loyers = retenus.stream().map(Retenu::loyerMensuel).toList();
            loyerCentral = Statistiques.mediane(loyers);
            loyerBas = Statistiques.quantile(loyers, QUARTILE_BAS);
            loyerHaut = Statistiques.quantile(loyers, QUARTILE_HAUT);
            dispersion = Statistiques.coefficientVariation(loyers);
            nombre = retenus.size();
            sourceLoyer = "MARCHE";
            avertissementsMarche(retenus, selection.nuiteesIgnorees(), minimum, nuitsParMois, facteurMeuble,
                    avertissements);
            if (loyerBienUtilisable) {
                BigDecimal ecart = loyerBien.subtract(loyerCentral).abs().divide(loyerCentral, 4, RoundingMode.HALF_UP);
                detail.put("ecartLoyerBienMarche", ecart);
                if (ecart.compareTo(fraction(ClesParametres.CAPITALISATION_ECART_ALERTE_LOYER_BIEN)) > 0) {
                    avertissements.add("Le loyer actuel du bien (" + xof(loyerBien) + "/mois) s'écarte de "
                            + pourcentage(ecart) + " du loyer de marché (" + xof(loyerCentral)
                            + "/mois) : la valeur retenue repose sur le loyer de marché.");
                }
            }
        } else if (loyerBienUtilisable) {
            BigDecimal marge = fraction(ClesParametres.CAPITALISATION_MARGE_FOURCHETTE);
            if (marge.compareTo(BigDecimal.ONE) >= 0) {
                throw new IllegalStateException("Paramétrage incohérent : " + ClesParametres.CAPITALISATION_MARGE_FOURCHETTE
                        + " doit être inférieur à 1 (" + marge + ").");
            }
            loyerCentral = loyerBien;
            loyerBas = loyerBien.multiply(BigDecimal.ONE.subtract(marge));
            loyerHaut = loyerBien.multiply(BigDecimal.ONE.add(marge));
            nombre = 0;
            sourceLoyer = "LOYER_BIEN";
            avertissements.add("Loyers de marché insuffisants (" + retenus.size() + " retenu(s), minimum " + minimum
                    + ") : le loyer actuel du bien (" + xof(loyerBien) + "/mois) sert de base, avec une marge de "
                    + pourcentage(marge) + ".");
        } else {
            throw new DonneesInsuffisantesException(methode(),
                    "la recherche de loyers comparables n'a pas été concluante : " + retenus.size()
                            + " loyer(s) exploitable(s) trouvé(s) dans le quartier « " + quartier.getLibelle()
                            + " » (minimum requis : " + minimum + ")"
                            + (selection.nuiteesIgnorees() > 0
                                    ? ", " + selection.nuiteesIgnorees() + " annonce(s) à la nuitée ignorée(s)" : "")
                            + " et aucun loyer actuel renseigné sur le bien.");
        }

        // ----- Capitalisation -----
        BigDecimal valeurCentrale = capitaliser(loyerCentral, tauxCharges, taux);
        BigDecimal valeurBasse = capitaliser(loyerBas, tauxCharges, taux);
        BigDecimal valeurHaute = capitaliser(loyerHaut, tauxCharges, taux);
        BigDecimal confiance = calculerConfiance(nombre, maximum, dispersion, tauxLocal, "LOYER_BIEN".equals(sourceLoyer));

        BigDecimal loyerAnnuelBrut = loyerCentral.multiply(MOIS_PAR_AN);
        detail.put("quartier", quartier.getCode());
        detail.put("sourceLoyer", sourceLoyer);
        detail.put("fenetreJours", fenetreJours);
        detail.put("nombreLoyersRetenus", retenus.size());
        detail.put("nuiteesIgnorees", selection.nuiteesIgnorees());
        detail.put("nuitsParMois", nuitsParMois);
        detail.put("facteurMeuble", facteurMeuble);
        detail.put("comparables", lignesLoyers(retenus));
        detail.put("loyerMensuelCentral", loyerCentral);
        detail.put("loyerMensuelBas", loyerBas);
        detail.put("loyerMensuelHaut", loyerHaut);
        detail.put("coefficientVariation", dispersion);
        detail.put("loyerMensuelActuelBien", loyerBienUtilisable ? loyerBien : null);
        detail.put("tauxCharges", tauxCharges);
        detail.put("loyerAnnuelBrut", loyerAnnuelBrut);
        detail.put("loyerAnnuelNet", loyerAnnuelBrut.multiply(BigDecimal.ONE.subtract(tauxCharges)));
        detail.put("tauxCapitalisation", taux);
        detail.put("tauxSource", tauxLocal ? "QUARTIER" : "DEFAUT_GLOBAL");
        detail.put("valeurCapitalisee", valeurCentrale);
        detail.put("indiceConfiance", confiance);

        return new ResultatMethodeCalcule(methode(),
                ResultatMethodeCalcule.arrondiXof(valeurBasse),
                ResultatMethodeCalcule.arrondiXof(valeurCentrale),
                ResultatMethodeCalcule.arrondiXof(valeurHaute),
                confiance, detail, avertissements);
    }

    // ---------- Recherche et normalisation des loyers ----------

    private Selection rechercherLoyers(Quartier quartier, int fenetreJours, int maximum,
                                       BigDecimal nuitsParMois, BigDecimal facteurMeuble) {
        LocalDate depuis = LocalDate.now(FUSEAU).minusDays(fenetreJours);
        List<AnnonceMarche> trouvees = annonceRepository.findLoyersComparables(
                quartier.getId(), NatureBien.BATI, TypeAnnonce.LOCATION, depuis);

        List<AnnonceMarche> recentesDabord = trouvees.stream()
                .sorted(Comparator.comparing(AnnonceMarche::getDateCollecte, Comparator.<LocalDate>reverseOrder()))
                .toList();

        List<Retenu> retenus = new ArrayList<>();
        int nuiteesIgnorees = 0;
        for (AnnonceMarche a : recentesDabord) {
            if (a.getMontant() == null || a.getMontant().signum() <= 0 || a.getPeriodicite() == null) {
                continue;
            }
            boolean nuitee = a.getPeriodicite() == PeriodiciteLoyer.NUITEE;
            BigDecimal loyer;
            if (nuitee) {
                if (nuitsParMois.signum() == 0) {
                    nuiteesIgnorees++;
                    continue;
                }
                loyer = a.getMontant().multiply(nuitsParMois);
            } else {
                loyer = a.getMontant();
            }
            boolean meuble = Boolean.TRUE.equals(a.getMeuble());
            if (meuble) {
                loyer = loyer.multiply(facteurMeuble);
            }
            retenus.add(new Retenu(a, loyer.setScale(2, RoundingMode.HALF_UP), nuitee, meuble));
        }
        List<Retenu> limites = retenus.size() > maximum ? retenus.subList(0, maximum) : retenus;
        return new Selection(List.copyOf(limites), nuiteesIgnorees);
    }

    private void avertissementsMarche(List<Retenu> retenus, int nuiteesIgnorees, int minimum,
                                      BigDecimal nuitsParMois, BigDecimal facteurMeuble, List<String> avertissements) {
        long nuitees = retenus.stream().filter(Retenu::convertiDepuisNuitee).count();
        long meubles = retenus.stream().filter(Retenu::ajusteMeuble).count();
        if (retenus.size() == minimum) {
            avertissements.add("Nombre de loyers au minimum requis (" + minimum + ") : fourchette peu robuste.");
        }
        if (nuitees > 0) {
            avertissements.add(nuitees + " loyer(s) converti(s) depuis un tarif à la nuitée ("
                    + nuitsParMois.stripTrailingZeros().toPlainString() + " nuits payées par mois) : hypothèse "
                    + "d'occupation à valider, ces biens ne sont pas strictement comparables à une location longue durée.");
        }
        if (meubles > 0) {
            avertissements.add(meubles + " loyer(s) meublé(s) ramené(s) à un équivalent non meublé (facteur "
                    + facteurMeuble.stripTrailingZeros().toPlainString() + ").");
        }
        if (nuiteesIgnorees > 0) {
            avertissements.add(nuiteesIgnorees + " annonce(s) à la nuitée ignorée(s) : conversion désactivée ("
                    + ClesParametres.CAPITALISATION_NUITEES_PAR_MOIS + " = 0).");
        }
    }

    private List<Map<String, Object>> lignesLoyers(List<Retenu> retenus) {
        List<Map<String, Object>> lignes = new ArrayList<>();
        for (Retenu r : retenus) {
            AnnonceMarche a = r.annonce();
            Map<String, Object> ligne = new LinkedHashMap<>();
            ligne.put("id", a.getId());
            ligne.put("origine", a.getOrigine() != null ? a.getOrigine().name() : null);
            ligne.put("source", a.getSource());
            ligne.put("urlSource", a.getUrlSource());
            ligne.put("titre", a.getTitre());
            ligne.put("periodicite", a.getPeriodicite().name());
            ligne.put("meuble", a.getMeuble());
            ligne.put("montantAnnonce", a.getMontant());
            ligne.put("loyerMensuelNormalise", r.loyerMensuel());
            ligne.put("convertiDepuisNuitee", r.convertiDepuisNuitee());
            ligne.put("ajusteMeuble", r.ajusteMeuble());
            ligne.put("dateCollecte", a.getDateCollecte() != null ? a.getDateCollecte().toString() : null);
            lignes.add(ligne);
        }
        return lignes;
    }

    // ---------- Valeur et confiance ----------

    private BigDecimal capitaliser(BigDecimal loyerMensuel, BigDecimal tauxCharges, BigDecimal taux) {
        BigDecimal loyerAnnuelNet = loyerMensuel.multiply(MOIS_PAR_AN).multiply(BigDecimal.ONE.subtract(tauxCharges));
        return loyerAnnuelNet.divide(taux, 2, RoundingMode.HALF_UP);
    }

    /**
     * Confiance = min + (max − min) × volume × (1 − pénalité × dispersion), où volume = loyers / maximum.
     * Multipliée par un facteur si le taux est celui par défaut, et par un autre si la base est le loyer du bien.
     */
    private BigDecimal calculerConfiance(int nombre, int maximum, BigDecimal dispersion,
                                         boolean tauxLocal, boolean loyerDuBien) {
        BigDecimal min = fraction(ClesParametres.CAPITALISATION_CONFIANCE_MIN);
        BigDecimal max = fraction(ClesParametres.CAPITALISATION_CONFIANCE_MAX);
        BigDecimal facteurTaux = fraction(ClesParametres.CAPITALISATION_CONFIANCE_FACTEUR_TAUX_DEFAUT);
        BigDecimal facteurBien = fraction(ClesParametres.CAPITALISATION_CONFIANCE_FACTEUR_LOYER_BIEN);
        BigDecimal penalite = parametres.valeur(ClesParametres.CAPITALISATION_PENALITE_DISPERSION);
        if (min.compareTo(max) > 0 || penalite.signum() < 0) {
            throw new IllegalStateException("Paramétrage incohérent : il faut " + ClesParametres.CAPITALISATION_CONFIANCE_MIN
                    + " <= " + ClesParametres.CAPITALISATION_CONFIANCE_MAX + " et "
                    + ClesParametres.CAPITALISATION_PENALITE_DISPERSION + " >= 0.");
        }
        BigDecimal volume = BigDecimal.valueOf(nombre)
                .divide(BigDecimal.valueOf(maximum), 4, RoundingMode.HALF_UP).min(BigDecimal.ONE);
        BigDecimal facteurDispersion = BigDecimal.ONE.subtract(penalite.multiply(dispersion)).max(BigDecimal.ZERO);
        BigDecimal confiance = min.add(max.subtract(min).multiply(volume).multiply(facteurDispersion));
        if (loyerDuBien) {
            confiance = confiance.multiply(facteurBien);
        }
        if (!tauxLocal) {
            confiance = confiance.multiply(facteurTaux);
        }
        return confiance.max(BigDecimal.ZERO).min(BigDecimal.ONE).setScale(4, RoundingMode.HALF_UP);
    }

    // ---------- Utilitaires ----------

    private BigDecimal fraction(String cle) {
        BigDecimal valeur = parametres.valeur(cle);
        if (valeur.signum() < 0 || valeur.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("Paramétrage incohérent : " + cle
                    + " doit être compris entre 0 et 1 (" + valeur + ").");
        }
        return valeur;
    }

    private static String pourcentage(BigDecimal fraction) {
        return fraction.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP) + " %";
    }

    private static String xof(BigDecimal montant) {
        return ResultatMethodeCalcule.arrondiXof(montant).toPlainString() + " XOF";
    }
}