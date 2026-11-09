package tg.nassara.estimation.service.parametrage;

import tg.nassara.estimation.exception.DonneesInvalidesException;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** Valeurs brutes reçues de l'API, lues avec validation. */
public final class ValeursSaisies {

    private final Map<String, String> valeurs;

    public ValeursSaisies(Map<String, String> valeurs) {
        this.valeurs = valeurs == null ? Map.of() : new LinkedHashMap<>(valeurs);
    }

    public Set<String> noms() {
        return valeurs.keySet();
    }

    public String valeurBrute(String nom) {
        return valeurs.get(nom);
    }

    public String texte(String nom) {
        return Convertisseurs.texteObligatoire(nom, valeurs.get(nom));
    }

    public String texteOptionnel(String nom) {
        return Convertisseurs.texteOptionnel(valeurs.get(nom));
    }

    public BigDecimal decimal(String nom) {
        return Convertisseurs.decimal(nom, valeurs.get(nom));
    }

    public <E extends Enum<E>> E enumeration(String nom, Class<E> type) {
        return Convertisseurs.enumeration(nom, type, valeurs.get(nom));
    }
}