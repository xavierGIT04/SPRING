package tg.nassara.estimation.dto.response;

import tg.nassara.estimation.enums.GenreChamp;
import tg.nassara.estimation.service.parametrage.ChampParametre;

import java.util.List;

/** Description d'un champ de paramètre : permet au front de générer ses formulaires dynamiquement. */
public record ChampDescriptionResponse(String nom, GenreChamp genre, boolean immuable, boolean activation,
                                       boolean optionnel, List<String> valeurs) {

    public static ChampDescriptionResponse depuis(ChampParametre<?> c) {
        return new ChampDescriptionResponse(c.nom(), c.genre(), c.immuable(), c.activation(), c.optionnel(),
                c.valeursPossibles());
    }
}
