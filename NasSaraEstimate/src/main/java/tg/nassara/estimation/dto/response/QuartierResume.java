package tg.nassara.estimation.dto.response;

import java.math.BigDecimal;

public record QuartierResume(Long id, String code, String libelle,
                             BigDecimal prixBaseTerrainM2, boolean prixBaseProvisoire) {
}