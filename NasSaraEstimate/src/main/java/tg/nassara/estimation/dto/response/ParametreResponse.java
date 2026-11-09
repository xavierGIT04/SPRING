package tg.nassara.estimation.dto.response;

import java.util.Map;

public record ParametreResponse(Long id, String type, Long version, Map<String, Object> valeurs) {
}