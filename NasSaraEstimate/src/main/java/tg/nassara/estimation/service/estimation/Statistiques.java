package tg.nassara.estimation.service.estimation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Statistiques simples sur des prix au m² (médiane, quantiles interpolés, dispersion). */
final class Statistiques {

    private Statistiques() {
    }

    static BigDecimal mediane(List<BigDecimal> valeurs) {
        return quantile(valeurs, new BigDecimal("0.5"));
    }

    /** Quantile par interpolation linéaire (p entre 0 et 1). Avec deux valeurs, Q1 et Q3 sont à 25 % et 75 % de l'écart. */
    static BigDecimal quantile(List<BigDecimal> valeurs, BigDecimal p) {
        if (valeurs.isEmpty()) {
            throw new IllegalArgumentException("Aucune valeur.");
        }
        List<BigDecimal> tri = valeurs.stream().sorted().toList();
        if (tri.size() == 1) {
            return tri.get(0);
        }
        BigDecimal position = p.multiply(BigDecimal.valueOf(tri.size() - 1L));
        int bas = position.setScale(0, RoundingMode.FLOOR).intValue();
        int haut = Math.min(bas + 1, tri.size() - 1);
        BigDecimal fraction = position.subtract(BigDecimal.valueOf(bas));
        BigDecimal ecart = tri.get(haut).subtract(tri.get(bas));
        return tri.get(bas).add(ecart.multiply(fraction));
    }

    /** Écart-type (population) rapporté à la moyenne ; 0 s'il y a moins de deux valeurs. */
    static BigDecimal coefficientVariation(List<BigDecimal> valeurs) {
        if (valeurs.size() < 2) {
            return BigDecimal.ZERO;
        }
        double moyenne = valeurs.stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0);
        if (moyenne <= 0) {
            return BigDecimal.ZERO;
        }
        double variance = valeurs.stream()
                .mapToDouble(v -> Math.pow(v.doubleValue() - moyenne, 2))
                .average().orElse(0);
        return BigDecimal.valueOf(Math.sqrt(variance) / moyenne).setScale(4, RoundingMode.HALF_UP);
    }
}
