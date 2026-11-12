package tg.nassara.estimation.dto.response;

import java.util.List;

/**
 * Décrit un type de paramètre gérable : utile pour générer dynamiquement les écrans d'administration du CRM.
 * « champs » (étape 8 bis) donne le genre de chaque champ ; les deux listes de noms sont conservées pour compatibilité.
 */
public record TypeParametreResponse(String type, List<String> champsModifiables, List<String> champsImmuables,
                                    List<ChampDescriptionResponse> champs) {
}
