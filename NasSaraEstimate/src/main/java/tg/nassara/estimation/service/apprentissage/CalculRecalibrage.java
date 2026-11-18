package tg.nassara.estimation.service.apprentissage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Formules pures du recalibrage (sans accès à la base), testables isolément. */
final class CalculRecalibrage {

    private CalculRecalibrage() {
    }

    /** Coefficient retenu et, le cas échéant, la limite qui l'a borné. */
    record MiseAJour(BigDecimal nouveau, String limite) {
    }

    static BigDecimal mediane(List<BigDecimal> valeurs) {
        if (valeurs.isEmpty()) {
            throw new IllegalArgumentException("Aucune valeur.");
        }
        List<BigDecimal> tri = valeurs.stream().sorted().toList();
        int n = tri.size();
        if (n % 2 == 1) {
            return tri.get(n / 2);
        }
        return tri.get(n / 2 - 1).add(tri.get(n / 2)).divide(BigDecimal.valueOf(2), 6, RoundingMode.HALF_UP);
    }

    /**
     * cible = ancien × (1 + lissage × (ratio − 1)) ; puis variation limitée à ± variationMax autour de l'ancien ;
     * puis bornes absolues. Résultat arrondi à 4 décimales (précision de la colonne).
     */
    static MiseAJour nouveauCoefficient(BigDecimal ancien, BigDecimal ratio, BigDecimal lissage,
                                        BigDecimal variationMax, BigDecimal min, BigDecimal max) {
        BigDecimal retenu = ancien.multiply(BigDecimal.ONE.add(lissage.multiply(ratio.subtract(BigDecimal.ONE))));
        BigDecimal bas = ancien.multiply(BigDecimal.ONE.subtract(variationMax));
        BigDecimal haut = ancien.multiply(BigDecimal.ONE.add(variationMax));
        String limite = null;

        if (retenu.compareTo(bas) < 0) {
            retenu = bas;
            limite = "VARIATION_MAX";
        } else if (retenu.compareTo(haut) > 0) {
            retenu = haut;
            limite = "VARIATION_MAX";
        }
        if (retenu.compareTo(min) < 0) {
            retenu = min;
            limite = "BORNE_MIN";
        } else if (retenu.compareTo(max) > 0) {
            retenu = max;
            limite = "BORNE_MAX";
        }
        return new MiseAJour(retenu.setScale(4, RoundingMode.HALF_UP), limite);
    }
}