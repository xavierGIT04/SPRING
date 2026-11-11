package tg.nassara.estimation.service.estimation;

import org.junit.jupiter.api.Test;
import tg.nassara.estimation.enums.MethodeEstimation;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JsonSimpleTest {

    @Test
    void serialiseMapListeNombresEtNull() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("montant", new BigDecimal("1500000"));
        map.put("texte", "a\"b");
        map.put("liste", List.of(1, true));
        map.put("vide", null);

        assertThat(JsonSimple.versJson(map))
                .isEqualTo("{\"montant\":1500000,\"texte\":\"a\\\"b\",\"liste\":[1,true],\"vide\":null}");
    }

    @Test
    void serialiseEnumerationRetoursLigneEtNombresNonFinis() {
        assertThat(JsonSimple.versJson(MethodeEstimation.HEDONIQUE)).isEqualTo("\"HEDONIQUE\"");
        assertThat(JsonSimple.versJson("a\nb")).isEqualTo("\"a\\nb\"");
        assertThat(JsonSimple.versJson(Double.NaN)).isEqualTo("null");
    }

    @Test
    void neUtilisePasLaNotationScientifiquePourLesDecimaux() {
        assertThat(JsonSimple.versJson(new BigDecimal("1E+7").add(BigDecimal.ZERO))).isEqualTo("10000000");
    }
}
