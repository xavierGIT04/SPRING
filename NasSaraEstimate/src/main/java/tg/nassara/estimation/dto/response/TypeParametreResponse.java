package tg.nassara.estimation.dto.response;

import java.util.List;

/** Décrit un type de paramètre gérable : utile pour générer dynamiquement les écrans d'administration du CRM. */
public record TypeParametreResponse(String type, List<String> champsModifiables, List<String> champsImmuables) {
}
