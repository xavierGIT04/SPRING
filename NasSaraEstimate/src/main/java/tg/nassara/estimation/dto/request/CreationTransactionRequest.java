package tg.nassara.estimation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Vente réelle de l'agence. L'estimation préalable (facultative) doit concerner le même bien : elle permet de
 * mesurer l'écart entre estimation et réalité. Le loyer constaté ne concerne que les biens bâtis locatifs.
 */
public record CreationTransactionRequest(
        @NotNull Long bienId,
        Long estimationId,
        @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 13, fraction = 2) BigDecimal prixVenteEffectif,
        @DecimalMin(value = "0", inclusive = false) @Digits(integer = 13, fraction = 2) BigDecimal loyerMensuelConstate,
        @NotNull @PastOrPresent LocalDate dateTransaction) {
}