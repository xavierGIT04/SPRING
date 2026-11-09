package tg.nassara.estimation.service.estimation;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.entity.BienBati;
import tg.nassara.estimation.entity.Equipement;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.entity.reference.Standing;
import tg.nassara.estimation.entity.reference.TypeBien;
import tg.nassara.estimation.entity.reference.Viabilisation;
import tg.nassara.estimation.enums.MethodeEstimation;
import tg.nassara.estimation.exception.DonneesInsuffisantesException;

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
 * Méthode 1 : estimation hédonique (prix de base du quartier + coefficients d'ajustement).
 *
 * <p>Terrain nu : surface × prix de base × coefficient de zone × produit borné des coefficients de site.<br>
 * Bâti : valeur du terrain (même formule) + valeur de remplacement à neuf × (1 − vétusté) + équipements.
 *
 * <p>Tous les paramètres sont lus via {@link ParametresCalcul}. Aucune valeur par défaut silencieuse.
 */
@Component
public class EstimateurHedonique implements Estimateur {

    private static final ZoneId FUSEAU = ZoneId.of("Africa/Lome");

    private final ParametresCalcul parametres;

    public EstimateurHedonique(ParametresCalcul parametres) {
        this.parametres = parametres;
    }

    @Override
    public MethodeEstimation methode() {
        return MethodeEstimation.HEDONIQUE;
    }

    /** Applicable aux deux natures de bien ; l'activation relève des pondérations paramétrées. */
    @Override
    public boolean estApplicable(Bien bien) {
        return bien != null;
    }

    @Override
    public ResultatMethodeCalcule estimer(Bien bien) {
        Bien reel = (Bien) Hibernate.unproxy(bien);
        return switch (reel.nature()) {
            case TERRAIN -> estimerTerrain(reel);
            case BATI -> estimerBati((BienBati) reel);
        };
    }

    // ---------- Terrain nu ----------

    private ResultatMethodeCalcule estimerTerrain(Bien terrain) {
        List<String> avertissements = new ArrayList<>();
        Map<String, Object> detail = new LinkedHashMap<>();

        BigDecimal valeurTerrain = calculerTerrain(terrain, detail, avertissements);
        if (!terrain.getEquipements().isEmpty()) {
            avertissements.add("Les équipements saisis sur un terrain nu ne sont pas valorisés par la méthode hédonique.");
        }
        detail.put("valeurTotale", valeurTerrain);

        return construireResultat(terrain.getQuartier(), valeurTerrain, detail, avertissements);
    }

    // ---------- Bien bâti ----------

    private ResultatMethodeCalcule estimerBati(BienBati bati) {
        List<String> avertissements = new ArrayList<>();
        Map<String, Object> detail = new LinkedHashMap<>();

        BigDecimal valeurTerrain = calculerTerrain(bati, detail, avertissements);

        BigDecimal surfaceBatie = bati.getSurfaceBatieM2();
        if (surfaceBatie == null || surfaceBatie.signum() <= 0) {
            throw new DonneesInsuffisantesException(methode(), "surface bâtie absente ou nulle.");
        }
        Standing standing = bati.getStanding();
        if (standing == null) {
            throw new DonneesInsuffisantesException(methode(), "standing du bien non renseigné.");
        }
        TypeBien typeBien = bati.getTypeBien();
        if (typeBien == null) {
            throw new DonneesInsuffisantesException(methode(), "type de bien non renseigné.");
        }
        Integer anneeConstruction = bati.getAnneeConstruction();
        if (anneeConstruction == null) {
            throw new DonneesInsuffisantesException(methode(), "année de construction non renseignée.");
        }

        // Valeur de remplacement à neuf, ajustée par le coefficient du type de bien.
        BigDecimal valeurNeuf = surfaceBatie
                .multiply(standing.getCoutConstructionM2())
                .multiply(typeBien.getCoefficient());

        // Vétusté linéaire annuelle, plafonnée.
        int anneeCourante = LocalDate.now(FUSEAU).getYear();
        if (anneeConstruction > anneeCourante) {
            avertissements.add("Année de construction postérieure à l'année courante (" + anneeConstruction
                    + ") : bien considéré comme neuf.");
        }
        int age = Math.max(0, anneeCourante - anneeConstruction);
        BigDecimal vetusteBrute = standing.getTauxVetusteAnnuel().multiply(BigDecimal.valueOf(age));
        BigDecimal vetusteRetenue = vetusteBrute.min(standing.getPlafondVetuste());
        if (vetusteRetenue.compareTo(vetusteBrute) < 0) {
            avertissements.add("Vétusté plafonnée à " + pourcentage(standing.getPlafondVetuste())
                    + " (calcul brut : " + pourcentage(vetusteBrute) + ").");
        }
        BigDecimal valeurConstruction = valeurNeuf.multiply(BigDecimal.ONE.subtract(vetusteRetenue).max(BigDecimal.ZERO));

        // Équipements : montant propre, sinon montant du catalogue. Pas de vétusté appliquée.
        List<Map<String, Object>> lignesEquipements = new ArrayList<>();
        BigDecimal totalEquipements = BigDecimal.ZERO;
        for (Equipement equipement : bati.getEquipements()) {
            BigDecimal montant = equipement.montantRetenu();
            totalEquipements = totalEquipements.add(montant);
            Map<String, Object> ligne = new LinkedHashMap<>();
            ligne.put("type", equipement.getTypeEquipement().getCode());
            ligne.put("montant", montant);
            ligne.put("source", equipement.getMontant() != null ? "PROPRE" : "CATALOGUE");
            lignesEquipements.add(ligne);
        }

        Map<String, Object> detailConstruction = new LinkedHashMap<>();
        detailConstruction.put("surfaceBatieM2", surfaceBatie);
        detailConstruction.put("standing", standing.getCode());
        detailConstruction.put("coutConstructionM2", standing.getCoutConstructionM2());
        detailConstruction.put("typeBien", typeBien.getCode());
        detailConstruction.put("coefficientTypeBien", typeBien.getCoefficient());
        detailConstruction.put("valeurRemplacementNeuf", valeurNeuf);
        detailConstruction.put("anneeConstruction", anneeConstruction);
        detailConstruction.put("age", age);
        detailConstruction.put("vetusteBrute", vetusteBrute);
        detailConstruction.put("vetusteRetenue", vetusteRetenue);
        detailConstruction.put("valeurApresVetuste", valeurConstruction);
        detail.put("construction", detailConstruction);
        detail.put("equipements", lignesEquipements);
        detail.put("totalEquipements", totalEquipements);

        BigDecimal valeurTotale = valeurTerrain.add(valeurConstruction).add(totalEquipements);
        detail.put("valeurTotale", valeurTotale);

        return construireResultat(bati.getQuartier(), valeurTotale, detail, avertissements);
    }

    // ---------- Valeur du terrain (commune aux deux natures) ----------

    private BigDecimal calculerTerrain(Bien bien, Map<String, Object> detailGlobal, List<String> avertissements) {
        BigDecimal surface = bien.getSurfaceTerrainM2();
        if (surface == null || surface.signum() <= 0) {
            throw new DonneesInsuffisantesException(methode(), "surface du terrain absente ou nulle.");
        }
        Quartier quartier = bien.getQuartier();
        BigDecimal prixBase = quartier.getPrixBaseTerrainM2();
        BigDecimal coefficientZone = quartier.getCoefficientZone();

        if (quartier.isPrixBaseProvisoire()) {
            avertissements.add("Le prix de base du quartier « " + quartier.getLibelle()
                    + " » est provisoire (saisi à chaud, non confirmé).");
        }
        if (quartier.getNombreTransactionsCalibrage() == 0) {
            avertissements.add("Le coefficient de zone du quartier « " + quartier.getLibelle()
                    + " » n'a jamais été calibré sur des transactions réelles.");
        }

        // Coefficients de site : multiplication, puis bornage.
        Map<String, Object> coefficients = new LinkedHashMap<>();
        BigDecimal produit = BigDecimal.ONE;
        produit = produit.multiply(enregistrer(coefficients, "typeVoie",
                bien.getTypeVoie().getCode(), bien.getTypeVoie().getCoefficient()));
        produit = produit.multiply(enregistrer(coefficients, "topographie",
                bien.getTopographie().getCode(), bien.getTopographie().getCoefficient()));
        produit = produit.multiply(enregistrer(coefficients, "statutJuridique",
                bien.getStatutJuridique().getCode(), bien.getStatutJuridique().getCoefficient()));

        List<Map<String, Object>> lignesViabilisations = new ArrayList<>();
        List<Viabilisation> viabilisations = bien.getViabilisations().stream()
                .sorted(Comparator.comparing(Viabilisation::getCode))
                .toList();
        for (Viabilisation viabilisation : viabilisations) {
            produit = produit.multiply(viabilisation.getCoefficient());
            lignesViabilisations.add(ligneCoefficient(viabilisation.getCode(), viabilisation.getCoefficient()));
        }
        coefficients.put("viabilisations", lignesViabilisations);

        BigDecimal plancher = parametres.valeur(ClesParametres.HEDONIQUE_CUMUL_COEFFICIENTS_MIN);
        BigDecimal plafond = parametres.valeur(ClesParametres.HEDONIQUE_CUMUL_COEFFICIENTS_MAX);
        if (plancher.signum() <= 0 || plancher.compareTo(plafond) > 0) {
            throw new IllegalStateException("Paramétrage incohérent : " + ClesParametres.HEDONIQUE_CUMUL_COEFFICIENTS_MIN
                    + " (" + plancher + ") doit être > 0 et <= " + ClesParametres.HEDONIQUE_CUMUL_COEFFICIENTS_MAX
                    + " (" + plafond + ").");
        }
        BigDecimal produitRetenu = produit.max(plancher).min(plafond);
        boolean borne = produitRetenu.compareTo(produit) != 0;
        if (borne) {
            avertissements.add("Cumul des coefficients de site borné : calculé " + produit.setScale(4, RoundingMode.HALF_UP)
                    + ", retenu " + produitRetenu.setScale(4, RoundingMode.HALF_UP)
                    + " (bornes " + plancher + " à " + plafond + ").");
        }

        BigDecimal valeurTerrain = surface.multiply(prixBase).multiply(coefficientZone).multiply(produitRetenu);

        Map<String, Object> detailTerrain = new LinkedHashMap<>();
        detailTerrain.put("quartier", quartier.getCode());
        detailTerrain.put("surfaceTerrainM2", surface);
        detailTerrain.put("prixBaseTerrainM2", prixBase);
        detailTerrain.put("coefficientZone", coefficientZone);
        detailTerrain.put("coefficientsSite", coefficients);
        detailTerrain.put("produitCoefficientsCalcule", produit);
        detailTerrain.put("produitCoefficientsRetenu", produitRetenu);
        detailTerrain.put("produitBorne", borne);
        detailTerrain.put("valeurTerrain", valeurTerrain);
        detailGlobal.put("terrain", detailTerrain);

        return valeurTerrain;
    }

    // ---------- Résultat final, fourchette et confiance ----------

    private ResultatMethodeCalcule construireResultat(Quartier quartier, BigDecimal prixCentral,
                                                      Map<String, Object> detail, List<String> avertissements) {
        BigDecimal marge = parametres.valeur(ClesParametres.HEDONIQUE_MARGE_FOURCHETTE);
        BigDecimal confiance = calculerConfiance(quartier);
        detail.put("margeFourchette", marge);
        detail.put("indiceConfiance", confiance);
        return ResultatMethodeCalcule.autourDe(methode(), prixCentral, marge, confiance, detail, avertissements);
    }

    /**
     * Confiance = min + (max − min) × min(1, transactions de calibrage / cible), puis réduite par le facteur
     * « provisoire » si le prix de base du quartier n'est pas confirmé.
     */
    private BigDecimal calculerConfiance(Quartier quartier) {
        BigDecimal max = fraction(ClesParametres.HEDONIQUE_CONFIANCE_MAX);
        BigDecimal min = fraction(ClesParametres.HEDONIQUE_CONFIANCE_MIN);
        BigDecimal facteurProvisoire = fraction(ClesParametres.HEDONIQUE_CONFIANCE_FACTEUR_PROVISOIRE);
        BigDecimal cible = parametres.valeur(ClesParametres.HEDONIQUE_TRANSACTIONS_CALIBRAGE_CIBLE);
        if (min.compareTo(max) > 0) {
            throw new IllegalStateException("Paramétrage incohérent : " + ClesParametres.HEDONIQUE_CONFIANCE_MIN
                    + " (" + min + ") doit être <= " + ClesParametres.HEDONIQUE_CONFIANCE_MAX + " (" + max + ").");
        }
        if (cible.signum() <= 0) {
            throw new IllegalStateException("Paramétrage incohérent : "
                    + ClesParametres.HEDONIQUE_TRANSACTIONS_CALIBRAGE_CIBLE + " doit être strictement positif.");
        }
        BigDecimal ratio = BigDecimal.valueOf(quartier.getNombreTransactionsCalibrage())
                .divide(cible, 4, RoundingMode.HALF_UP)
                .min(BigDecimal.ONE);
        BigDecimal confiance = min.add(max.subtract(min).multiply(ratio));
        if (quartier.isPrixBaseProvisoire()) {
            confiance = confiance.multiply(facteurProvisoire);
        }
        return confiance.max(BigDecimal.ZERO).min(BigDecimal.ONE).setScale(4, RoundingMode.HALF_UP);
    }

    // ---------- Utilitaires ----------

    private BigDecimal fraction(String cle) {
        BigDecimal valeur = parametres.valeur(cle);
        if (valeur.signum() < 0 || valeur.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("Paramétrage incohérent : " + cle + " doit être compris entre 0 et 1 (" + valeur + ").");
        }
        return valeur;
    }

    private static BigDecimal enregistrer(Map<String, Object> coefficients, String nom, String code, BigDecimal coefficient) {
        coefficients.put(nom, ligneCoefficient(code, coefficient));
        return coefficient;
    }

    private static Map<String, Object> ligneCoefficient(String code, BigDecimal coefficient) {
        Map<String, Object> ligne = new LinkedHashMap<>();
        ligne.put("code", code);
        ligne.put("coefficient", coefficient);
        return ligne;
    }

    private static String pourcentage(BigDecimal fraction) {
        return fraction.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP) + " %";
    }
}