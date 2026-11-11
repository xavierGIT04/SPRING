package tg.nassara.estimation.dto.response;

import tg.nassara.estimation.entity.Estimation;
import tg.nassara.estimation.enums.StatutEstimation;

import java.math.BigDecimal;
import java.time.Instant;

/** Ligne de l'historique des estimations d'un bien (sans le détail par méthode). */
public record EstimationResumeResponse(
        Long id,
        StatutEstimation statut,
        BigDecimal prixMin,
        BigDecimal prixCentral,
        BigDecimal prixMax,
        BigDecimal indiceConfiance,
        Instant dateCreation,
        String demandePar) {

    public static EstimationResumeResponse depuis(Estimation e) {
        return new EstimationResumeResponse(e.getId(), e.getStatut(), e.getPrixMin(), e.getPrixCentral(),
                e.getPrixMax(), e.getIndiceConfiance(), e.getDateCreation(), e.getDemandePar());
    }
}
