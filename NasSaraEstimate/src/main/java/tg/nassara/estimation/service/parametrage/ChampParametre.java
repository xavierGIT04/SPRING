package tg.nassara.estimation.service.parametrage;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Un champ d'un paramètre : comment le lire, et comment l'écrire à partir d'un texte (avec validation).
 * Le même descripteur sert à la création, à la modification, à l'historique et à la restauration.
 */
public record ChampParametre<T>(
        String nom,
        Function<T, Object> lecteur,
        BiConsumer<T, String> ecriveur,
        boolean activation,
        boolean immuable) {

    public static <T> ChampParametre<T> texte(String nom, Function<T, String> lecteur, BiConsumer<T, String> ecriveur) {
        return new ChampParametre<T>(nom, e -> lecteur.apply(e),
                (e, v) -> ecriveur.accept(e, Convertisseurs.texteObligatoire(nom, v)), false, false);
    }

    public static <T> ChampParametre<T> texteOptionnel(String nom, Function<T, String> lecteur, BiConsumer<T, String> ecriveur) {
        return new ChampParametre<T>(nom, e -> lecteur.apply(e),
                (e, v) -> ecriveur.accept(e, Convertisseurs.texteOptionnel(v)), false, false);
    }

    /** Identifiant stable (code, clé) : fixé à la création, jamais modifiable. */
    public static <T> ChampParametre<T> texteImmuable(String nom, Function<T, String> lecteur, BiConsumer<T, String> ecriveur) {
        return new ChampParametre<T>(nom, e -> lecteur.apply(e),
                (e, v) -> ecriveur.accept(e, Convertisseurs.texteObligatoire(nom, v)), false, true);
    }

    public static <T> ChampParametre<T> decimalPositif(String nom, Function<T, BigDecimal> lecteur, BiConsumer<T, BigDecimal> ecriveur) {
        return new ChampParametre<T>(nom, e -> lecteur.apply(e),
                (e, v) -> ecriveur.accept(e, Convertisseurs.decimalPositif(nom, v)), false, false);
    }

    public static <T> ChampParametre<T> decimalNonNegatif(String nom, Function<T, BigDecimal> lecteur, BiConsumer<T, BigDecimal> ecriveur) {
        return new ChampParametre<T>(nom, e -> lecteur.apply(e),
                (e, v) -> ecriveur.accept(e, Convertisseurs.decimalNonNegatif(nom, v)), false, false);
    }

    public static <T> ChampParametre<T> decimalPositifOptionnel(String nom, Function<T, BigDecimal> lecteur, BiConsumer<T, BigDecimal> ecriveur) {
        return new ChampParametre<T>(nom, e -> lecteur.apply(e),
                (e, v) -> ecriveur.accept(e,
                        Convertisseurs.texteOptionnel(v) == null ? null : Convertisseurs.decimalPositif(nom, v)),
                false, false);
    }

    public static <T> ChampParametre<T> booleen(String nom, Function<T, Boolean> lecteur, BiConsumer<T, Boolean> ecriveur) {
        return new ChampParametre<T>(nom, e -> lecteur.apply(e),
                (e, v) -> ecriveur.accept(e, Convertisseurs.booleen(nom, v)), false, false);
    }

    /** Champ d'activation (« actif » / « active ») : passer à false est tracé comme une DESACTIVATION. */
    public static <T> ChampParametre<T> activation(String nom, Function<T, Boolean> lecteur, BiConsumer<T, Boolean> ecriveur) {
        return new ChampParametre<T>(nom, e -> lecteur.apply(e),
                (e, v) -> ecriveur.accept(e, Convertisseurs.booleen(nom, v)), true, false);
    }

    public static <T, E extends Enum<E>> ChampParametre<T> enumerationImmuable(
            String nom, Class<E> type, Function<T, E> lecteur, BiConsumer<T, E> ecriveur) {
        return new ChampParametre<T>(nom, e -> lecteur.apply(e),
                (e, v) -> ecriveur.accept(e, Convertisseurs.enumeration(nom, type, v)), false, true);
    }
}