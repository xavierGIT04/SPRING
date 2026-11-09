package tg.nassara.estimation.service.estimation;

/**
 * Clés des paramètres globaux lus par les méthodes. Ce sont des identifiants techniques :
 * les VALEURS sont saisies par les agents (type « parametres-globaux »). Liste provisoire,
 * complétée aux étapes 4 à 6.
 */
public final class ClesParametres {

    private ClesParametres() {
    }

    // --- Méthode 1 : hédonique ---
    /** Fraction (ex. 0,10 = ±10 %) appliquée autour du prix central pour former la fourchette. */
    public static final String HEDONIQUE_MARGE_FOURCHETTE = "HEDONIQUE_MARGE_FOURCHETTE";

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