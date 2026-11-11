package tg.nassara.estimation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tg.nassara.estimation.enums.NatureBien;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Bien saisi par l'agent avant une estimation. Les références (quartier, voie, etc.) sont désignées par leur identifiant.
 * Terrain nu : seuls les champs du socle commun sont acceptés.
 * Bien bâti : type de bien, standing, surface bâtie et année de construction sont obligatoires ;
 * un loyer actuel n'est accepté que si le bien est locatif ou commercial.
 */
public record CreationBienRequest(
        @NotNull NatureBien nature,
        @NotNull Long quartierId,
        @Size(max = 255) String adresse,
        @DecimalMin("-90") @DecimalMax("90") @Digits(integer = 3, fraction = 7) BigDecimal latitude,
        @DecimalMin("-180") @DecimalMax("180") @Digits(integer = 3, fraction = 7) BigDecimal longitude,
        @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 10, fraction = 2) BigDecimal surfaceTerrainM2,
        @NotNull Long typeVoieId,
        @NotNull Long topographieId,
        @NotNull Long statutJuridiqueId,
        Set<@NotNull Long> viabilisationIds,
        @Valid @Size(max = 50) List<@NotNull @Valid EquipementRequest> equipements,
        // --- bien bâti uniquement ---
        Long typeBienId,
        Long standingId,
        @DecimalMin(value = "0", inclusive = false) @Digits(integer = 10, fraction = 2) BigDecimal surfaceBatieM2,
        Integer anneeConstruction,
        @Min(1) @Max(200) Integer nombreNiveaux,
        Boolean locatifOuCommercial,
        @DecimalMin(value = "0", inclusive = false) @Digits(integer = 13, fraction = 2) BigDecimal loyerMensuelActuel) {
}
