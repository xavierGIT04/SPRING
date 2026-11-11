package tg.nassara.estimation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Équipement d'un bien. Sans montant propre, le montant de référence du catalogue s'applique. */
public record EquipementRequest(
        @NotNull Long typeEquipementId,
        @DecimalMin(value = "0", inclusive = false) @Digits(integer = 13, fraction = 2) BigDecimal montant,
        @Size(max = 255) String description) {
}
