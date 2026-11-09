package tg.nassara.estimation.dto.response;

import tg.nassara.estimation.entity.HistoriqueParametre;
import tg.nassara.estimation.enums.OrigineModification;
import tg.nassara.estimation.enums.TypeOperation;

import java.time.Instant;

public record HistoriqueParametreResponse(
        Long id,
        String typeParametre,
        Long idParametre,
        String referenceParametre,
        TypeOperation operation,
        String champ,
        String ancienneValeur,
        String nouvelleValeur,
        OrigineModification origine,
        String modifiePar,
        Instant dateModification,
        String motif,
        Long restaureDepuisId) {

    public static HistoriqueParametreResponse depuis(HistoriqueParametre h) {
        return new HistoriqueParametreResponse(h.getId(), h.getTypeParametre(), h.getIdParametre(),
                h.getReferenceParametre(), h.getOperation(), h.getChamp(), h.getAncienneValeur(),
                h.getNouvelleValeur(), h.getOrigine(), h.getModifiePar(), h.getDateModification(),
                h.getMotif(), h.getRestaureDepuisId());
    }
}