package tg.nassara.estimation.dto.response;

import java.util.List;

public record PageResponse<T>(List<T> contenu, int page, int taille, long totalElements, int totalPages) {
}