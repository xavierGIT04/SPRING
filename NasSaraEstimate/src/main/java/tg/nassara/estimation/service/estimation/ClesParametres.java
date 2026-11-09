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

    /** Nombre minimal d'annonces comparables pour que la méthode conclue. */
    public static final String MARCHE_MIN_COMPARABLES = "MARCHE_MIN_COMPARABLES";

    // --- Méthode 3 : capitalisation ---
    /** Taux de capitalisation utilisé si le quartier n'a pas le sien (fraction, ex. 0,08 = 8 %). */
    public static final String CAPITALISATION_TAUX_DEFAUT = "CAPITALISATION_TAUX_DEFAUT";

    /** Part du loyer brut retirée pour obtenir le loyer net : vacance, charges, impôts (fraction). */
    public static final String CAPITALISATION_TAUX_CHARGES = "CAPITALISATION_TAUX_CHARGES";
}