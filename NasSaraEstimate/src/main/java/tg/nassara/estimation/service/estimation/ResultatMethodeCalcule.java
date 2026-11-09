package tg.nassara.estimation.service.estimation;

import tg.nassara.estimation.enums.MethodeEstimation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Résultat d'une méthode : fourchette de prix (XOF) et traçabilité.
 *
 * @param indiceConfiance entre 0 et 1 ; chaque méthode définit sa propre formule (volume et qualité des données)
 * @param detail          valeurs de paramètres utilisées, comparables retenus, etc. (sérialisé en JSON à l'étape 7)
 * @param avertissements  limites constatées (données rares, prix de base provisoire, etc.)
 */
public record ResultatMethodeCalcule(
        MethodeEstimation methode,
        BigDecimal prixMin,
        BigDecimal prixCentral,
        BigDecimal prixMax,
        BigDecimal indiceConfiance,
        Map<String, Object> detail,
        List<String> avertissements) {

    public ResultatMethodeCalcule {
        Objects.requireNonNull(methode, "La méthode est obligatoire.");
        Objects.requireNonNull(prixMin, "Le prix minimum est obligatoire.");
        Objects.requireNonNull(prixCentral, "Le prix central est obligatoire.");
        Objects.requireNonNull(prixMax, "Le prix maximum est obligatoire.");
        Objects.requireNonNull(indiceConfiance, "L'indice de confiance est obligatoire.");
        if (prixMin.signum() < 0 || prixMin.compareTo(prixCentral) > 0 || prixCentral.compareTo(prixMax) > 0) {
            throw new IllegalArgumentException(
                    "Fourchette incohérente : il faut 0 <= min <= central <= max (" + prixMin + " / "
                            + prixCentral + " / " + prixMax + ").");
        }
        if (indiceConfiance.signum() < 0 || indiceConfiance.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("L'indice de confiance doit être compris entre 0 et 1.");
        }
        detail = detail == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(detail));
        avertissements = avertissements == null ? List.of() : List.copyOf(avertissements);
    }

    /** Le franc CFA n'a pas de centimes : montants arrondis à l'unité. */
    public static BigDecimal arrondiXof(BigDecimal montant) {
        return montant.setScale(0, RoundingMode.HALF_UP);
    }

    /** Fourchette symétrique autour d'un prix central : central × (1 − marge) à central × (1 + marge). */
    public static ResultatMethodeCalcule autourDe(MethodeEstimation methode, BigDecimal prixCentral,
                                                  BigDecimal marge, BigDecimal indiceConfiance,
                                                  Map<String, Object> detail, List<String> avertissements) {
        if (marge == null || marge.signum() < 0 || marge.compareTo(BigDecimal.ONE) >= 0) {
            throw new IllegalArgumentException("La marge doit être comprise entre 0 (inclus) et 1 (exclu).");
        }
        return new ResultatMethodeCalcule(
                methode,
                arrondiXof(prixCentral.multiply(BigDecimal.ONE.subtract(marge))),
                arrondiXof(prixCentral),
                arrondiXof(prixCentral.multiply(BigDecimal.ONE.add(marge))),
                indiceConfiance, detail, avertissements);
    }
}