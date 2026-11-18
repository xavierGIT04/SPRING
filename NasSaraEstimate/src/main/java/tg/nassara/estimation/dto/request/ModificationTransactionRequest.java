package tg.nassara.estimation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Correction d'une transaction non validée : seuls les champs renseignés sont modifiés. « version » est facultative. */
public record ModificationTransactionRequest(
        Long version,
        @DecimalMin(value = "0", inclusive = false) @Digits(integer = 13, fraction = 2) BigDecimal prixVenteEffectif,
        @DecimalMin(value = "0", inclusive = false) @Digits(integer = 13, fraction = 2) BigDecimal loyerMensuelConstate,
        @PastOrPresent LocalDate dateTransaction) {
}