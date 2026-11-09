package tg.nassara.estimation.service.parametrage;

import tg.nassara.estimation.exception.DonneesInvalidesException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;

/** Conversions et validations de saisie, avec des messages d'erreur exploitables par l'agent. */
public final class Convertisseurs {

    private Convertisseurs() {
    }

    public static String texteObligatoire(String champ, String valeur) {
        if (valeur == null || valeur.isBlank()) {
            throw new DonneesInvalidesException("Le champ '" + champ + "' est obligatoire.");
        }
        return valeur.strip();
    }

    public static String texteOptionnel(String valeur) {
        return (valeur == null || valeur.isBlank()) ? null : valeur.strip();
    }

    public static BigDecimal decimal(String champ, String valeur) {
        String texte = texteObligatoire(champ, valeur);
        try {
            return new BigDecimal(texte.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new DonneesInvalidesException("Le champ '" + champ + "' doit être un nombre : " + texte);
        }
    }

    public static BigDecimal decimalPositif(String champ, String valeur) {
        BigDecimal nombre = decimal(champ, valeur);
        if (nombre.signum() <= 0) {
            throw new DonneesInvalidesException("Le champ '" + champ + "' doit être strictement positif.");
        }
        return nombre;
    }

    public static BigDecimal decimalNonNegatif(String champ, String valeur) {
        BigDecimal nombre = decimal(champ, valeur);
        if (nombre.signum() < 0) {
            throw new DonneesInvalidesException("Le champ '" + champ + "' ne peut pas être négatif.");
        }
        return nombre;
    }

    public static boolean booleen(String champ, String valeur) {
        String texte = texteObligatoire(champ, valeur).toLowerCase(Locale.ROOT);
        if (texte.equals("true")) {
            return true;
        }
        if (texte.equals("false")) {
            return false;
        }
        throw new DonneesInvalidesException("Le champ '" + champ + "' doit valoir true ou false : " + valeur);
    }

    public static <E extends Enum<E>> E enumeration(String champ, Class<E> type, String valeur) {
        String texte = texteObligatoire(champ, valeur);
        try {
            return Enum.valueOf(type, texte.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new DonneesInvalidesException("Valeur invalide pour '" + champ + "' : " + texte
                    + " (attendu : " + Arrays.toString(type.getEnumConstants()) + ")");
        }
    }

    /** Représentation textuelle stable pour l'historique (1.50 et 1.5 donnent le même texte). */
    public static String versTexte(Object valeur) {
        if (valeur == null) {
            return null;
        }
        if (valeur instanceof BigDecimal nombre) {
            return nombre.stripTrailingZeros().toPlainString();
        }
        return valeur.toString();
    }
}