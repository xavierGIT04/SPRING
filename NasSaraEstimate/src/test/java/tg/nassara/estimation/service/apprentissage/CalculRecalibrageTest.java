package tg.nassara.estimation.service.apprentissage;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculRecalibrageTest {

    private static final BigDecimal LISSAGE = bd("0.20");
    private static final BigDecimal VARIATION = bd("0.10");
    private static final BigDecimal MIN = bd("0.50");
    private static final BigDecimal MAX = bd("2.00");

    @Test
    void mediane_impaireEtPaire() {
        assertThat(CalculRecalibrage.mediane(List.of(bd("1.4"), bd("1.0"), bd("1.2")))).isEqualByComparingTo("1.2");
        assertThat(CalculRecalibrage.mediane(List.of(bd("1.0"), bd("1.2")))).isEqualByComparingTo("1.1");
        assertThatThrownBy(() -> CalculRecalibrage.mediane(List.of())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void ecartModere_estLisseSansBornage() {
        // 1,0000 × (1 + 0,20 × (1,2 − 1)) = 1,0400
        CalculRecalibrage.MiseAJour maj = CalculRecalibrage.nouveauCoefficient(
                bd("1.0000"), bd("1.2"), LISSAGE, VARIATION, MIN, MAX);

        assertThat(maj.nouveau()).isEqualByComparingTo("1.0400");
        assertThat(maj.limite()).isNull();
    }

    @Test
    void ecartTresFort_estLimiteParLaVariationMaximale() {
        // Cible 1,0 × (1 + 0,20 × 1) = 1,20 → plafonnée à +10 %.
        CalculRecalibrage.MiseAJour haut = CalculRecalibrage.nouveauCoefficient(
                bd("1.0000"), bd("2.0"), LISSAGE, VARIATION, MIN, MAX);
        // Cible 1,0 × (1 + 0,20 × (0,1 − 1)) = 0,82 → relevée à −10 %.
        CalculRecalibrage.MiseAJour bas = CalculRecalibrage.nouveauCoefficient(
                bd("1.0000"), bd("0.1"), LISSAGE, VARIATION, MIN, MAX);

        assertThat(haut.nouveau()).isEqualByComparingTo("1.1000");
        assertThat(haut.limite()).isEqualTo("VARIATION_MAX");
        assertThat(bas.nouveau()).isEqualByComparingTo("0.9000");
        assertThat(bas.limite()).isEqualTo("VARIATION_MAX");
    }

    @Test
    void bornesAbsolues_s_appliquentApresLaVariation() {
        // 1,95 × 1,08 = 2,106 (dans ±10 % : plafond de variation 2,145) → ramené à 2,00.
        CalculRecalibrage.MiseAJour haut = CalculRecalibrage.nouveauCoefficient(
                bd("1.9500"), bd("1.4"), LISSAGE, VARIATION, MIN, MAX);
        // 0,52 × 0,92 = 0,4784 (dans ±10 %) → relevé à 0,50.
        CalculRecalibrage.MiseAJour bas = CalculRecalibrage.nouveauCoefficient(
                bd("0.5200"), bd("0.6"), LISSAGE, VARIATION, MIN, MAX);

        assertThat(haut.nouveau()).isEqualByComparingTo("2.0000");
        assertThat(haut.limite()).isEqualTo("BORNE_MAX");
        assertThat(bas.nouveau()).isEqualByComparingTo("0.5000");
        assertThat(bas.limite()).isEqualTo("BORNE_MIN");
    }

    @Test
    void ratioUnitaire_laisseLeCoefficientInchange() {
        CalculRecalibrage.MiseAJour maj = CalculRecalibrage.nouveauCoefficient(
                bd("1.2345"), bd("1.0"), LISSAGE, VARIATION, MIN, MAX);

        assertThat(maj.nouveau()).isEqualByComparingTo("1.2345");
        assertThat(maj.limite()).isNull();
    }

    private static BigDecimal bd(String valeur) {
        return new BigDecimal(valeur);
    }
}