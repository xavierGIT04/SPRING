package tg.nassara.estimation.service.estimation;

/**
 * Clés des paramètres globaux lus par les méthodes. Ce sont des identifiants techniques :
 * les VALEURS sont saisies par les agents (type « parametres-globaux »). Liste complétée
 * aux étapes 5 et 6.
 */
public final class ClesParametres {

    private ClesParametres() {
    }

    // --- Méthode 1 : hédonique ---
    /** Fraction (ex. 0,10 = ±10 %) appliquée autour du prix central pour former la fourchette. */
    public static final String HEDONIQUE_MARGE_FOURCHETTE = "HEDONIQUE_MARGE_FOURCHETTE";

    /** Plancher du produit des coefficients de site (voie, topographie, statut, viabilisations). Ex. 0,40. */
    public static final String HEDONIQUE_CUMUL_COEFFICIENTS_MIN = "HEDONIQUE_CUMUL_COEFFICIENTS_MIN";

    /** Plafond du produit des coefficients de site. Ex. 1,80. */
    public static final String HEDONIQUE_CUMUL_COEFFICIENTS_MAX = "HEDONIQUE_CUMUL_COEFFICIENTS_MAX";

    /** Indice de confiance maximal de la méthode (quartier bien calibré), entre 0 et 1. */
    public static final String HEDONIQUE_CONFIANCE_MAX = "HEDONIQUE_CONFIANCE_MAX";

    /** Indice de confiance minimal (quartier jamais calibré), entre 0 et 1. */
    public static final String HEDONIQUE_CONFIANCE_MIN = "HEDONIQUE_CONFIANCE_MIN";

    /** Nombre de transactions de calibrage au-delà duquel la confiance atteint son maximum. */
    public static final String HEDONIQUE_TRANSACTIONS_CALIBRAGE_CIBLE = "HEDONIQUE_TRANSACTIONS_CALIBRAGE_CIBLE";

    /** Facteur multiplicatif (0 à 1) appliqué à la confiance si le prix de base du quartier est provisoire. */
    public static final String HEDONIQUE_CONFIANCE_FACTEUR_PROVISOIRE = "HEDONIQUE_CONFIANCE_FACTEUR_PROVISOIRE";

    // --- Méthode 2 : comparaison de marché ---
    /** Ancienneté maximale des annonces retenues, en jours. */
    public static final String MARCHE_FENETRE_JOURS = "MARCHE_FENETRE_JOURS";
    
    /** Nombre maximal d'annonces retenues (les plus proches en surface, puis les plus récentes). */
    public static final String MARCHE_MAX_COMPARABLES = "MARCHE_MAX_COMPARABLES";

    /** Écart de surface toléré autour de celle du bien (fraction, ex. 0,30 = ±30 %). */
    public static final String MARCHE_TOLERANCE_SURFACE = "MARCHE_TOLERANCE_SURFACE";

    /** Surface d'un lot en m² (saisie manuelle des annonces exprimées en lots). */
    public static final String MARCHE_SURFACE_LOT_M2 = "MARCHE_SURFACE_LOT_M2";

    /** Facteur appliqué aux prix demandés tant qu'il n'y a pas assez de transactions (ex. 0,90 = négociation de 10 %). */
    public static final String MARCHE_DECOTE_NEGOCIATION_DEFAUT = "MARCHE_DECOTE_NEGOCIATION_DEFAUT";

    /** Nombre de transactions validées du quartier requis pour calibrer la correction. */
    public static final String MARCHE_MIN_TRANSACTIONS_CORRECTION = "MARCHE_MIN_TRANSACTIONS_CORRECTION";

    /** Bornes du facteur de correction calculé à partir des transactions. */
    public static final String MARCHE_CORRECTION_MIN = "MARCHE_CORRECTION_MIN";
    public static final String MARCHE_CORRECTION_MAX = "MARCHE_CORRECTION_MAX";

    /** Confiance minimale et maximale de la méthode (entre 0 et 1). */
    public static final String MARCHE_CONFIANCE_MIN = "MARCHE_CONFIANCE_MIN";
    public static final String MARCHE_CONFIANCE_MAX = "MARCHE_CONFIANCE_MAX";

    /** Poids de la dispersion des prix dans la confiance (0 = ignorée ; 1 = un écart-type de 100 % annule la confiance). */
    public static final String MARCHE_PENALITE_DISPERSION = "MARCHE_PENALITE_DISPERSION";

    /** Facteur (0 à 1) appliqué à la confiance quand la correction est la décote par défaut. */
    public static final String MARCHE_CONFIANCE_FACTEUR_DECOTE_DEFAUT = "MARCHE_CONFIANCE_FACTEUR_DECOTE_DEFAUT";

    /** Nombre minimal d'annonces comparables pour que la méthode conclue. */
    public static final String MARCHE_MIN_COMPARABLES = "MARCHE_MIN_COMPARABLES";

    // --- Méthode 3 : capitalisation ---
    /** Taux de capitalisation utilisé si le quartier n'a pas le sien (fraction, ex. 0,08 = 8 %). */
    public static final String CAPITALISATION_TAUX_DEFAUT = "CAPITALISATION_TAUX_DEFAUT";

    /** Part du loyer brut retirée pour obtenir le loyer net : vacance, charges, impôts (fraction). */
    public static final String CAPITALISATION_TAUX_CHARGES = "CAPITALISATION_TAUX_CHARGES";
}