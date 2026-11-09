package tg.nassara.estimation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Création minimale d'un quartier inconnu : un libellé et un prix de base initial. */
public record CreationQuartierRequest(
        @NotBlank @Size(max = 150) String libelle,
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal prixBaseTerrainM2) {
}
