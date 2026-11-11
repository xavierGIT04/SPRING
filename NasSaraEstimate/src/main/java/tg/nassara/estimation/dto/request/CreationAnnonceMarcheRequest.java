package tg.nassara.estimation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.TypeAnnonce;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Annonce trouvée par un agent. Pour une vente : surface du terrain en m² OU nombre de lots (jamais les deux).
 * Ne saisir que des faits (prix, surface, quartier) : ni texte copié, ni photo, ni numéro de téléphone.
 */
public record CreationAnnonceMarcheRequest(
        @NotBlank @Size(max = 80) String source,
        @Size(max = 600) String urlSource,
        @Size(max = 255) String titre,
        @NotNull TypeAnnonce typeAnnonce,
        @NotNull NatureBien nature,
        Long typeBienId,
        @NotNull Long quartierId,
        @DecimalMin(value = "0", inclusive = false) BigDecimal surfaceTerrainM2,
        @DecimalMin(value = "0", inclusive = false) BigDecimal nombreLots,
        @DecimalMin(value = "0", inclusive = false) BigDecimal surfaceBatieM2,
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal montant,
        @PastOrPresent LocalDate datePublication) {
}
