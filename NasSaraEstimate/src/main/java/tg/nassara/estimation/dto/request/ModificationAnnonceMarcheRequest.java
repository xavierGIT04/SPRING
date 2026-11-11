package tg.nassara.estimation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import tg.nassara.estimation.enums.PeriodiciteLoyer;

import java.math.BigDecimal;

/** Correction d'une saisie manuelle : seuls les champs renseignés sont modifiés. « version » est optionnelle. */
public record ModificationAnnonceMarcheRequest(
        Long version,
        @DecimalMin(value = "0", inclusive = false) BigDecimal montant,
        @DecimalMin(value = "0", inclusive = false) BigDecimal surfaceTerrainM2,
        @DecimalMin(value = "0", inclusive = false) BigDecimal surfaceBatieM2,
        PeriodiciteLoyer periodicite,
        Boolean meuble) {
}