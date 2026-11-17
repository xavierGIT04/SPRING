package tg.nassara.estimation.service.estimation;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogueParametresGlobauxTest {

    @Test
    void chaqueCleDeCalculEstCatalogueeUneSeuleFois() throws IllegalAccessException {
        Set<String> cles = new HashSet<>();
        for (Field champ : ClesParametres.class.getDeclaredFields()) {
            if (Modifier.isStatic(champ.getModifiers()) && champ.getType() == String.class) {
                cles.add((String) champ.get(null));
            }
        }
        Set<String> cataloguees = new HashSet<>();
        for (CatalogueParametresGlobaux.Entree e : CatalogueParametresGlobaux.ENTREES) {
            assertThat(cataloguees.add(e.cle())).as("doublon " + e.cle()).isTrue();
        }
        assertThat(cataloguees).isEqualTo(cles);
    }

    @Test
    void descriptionsEtUnitesRespectentLesLimitesDeColonnes() {
        for (CatalogueParametresGlobaux.Entree e : CatalogueParametresGlobaux.ENTREES) {
            assertThat(e.description().length()).as(e.cle()).isLessThanOrEqualTo(255);
            assertThat(e.unite().length()).as(e.cle()).isLessThanOrEqualTo(30);
        }
    }
}