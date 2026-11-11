package tg.nassara.estimation.dto.response;

import org.hibernate.Hibernate;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.entity.Estimation;
import tg.nassara.estimation.entity.ResultatMethode;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.StatutEstimation;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * Estimation finale : fourchette fusionnée, avertissements et détail par méthode.
 * Construite à partir de l'estimation ENREGISTRÉE, donc identique à la création et à la consultation
 * (à appeler dans une transaction ouverte : le bien et les résultats sont chargés à la demande).
 */
public record EstimationResponse(
        Long id,
        Long bienId,
        NatureBien nature,
        StatutEstimation statut,
        BigDecimal prixMin,
        BigDecimal prixCentral,
        BigDecimal prixMax,
        BigDecimal indiceConfiance,
        Instant dateCreation,
        String demandePar,
        List<String> avertissements,
        List<ResultatMethodeResponse> methodes) {

    public static EstimationResponse depuis(Estimation e) {
        Bien bien = (Bien) Hibernate.unproxy(e.getBien());
        String texte = e.getAvertissements();
        List<String> avertissements = (texte == null || texte.isBlank()) ? List.of() : List.of(texte.split("\n"));
        List<ResultatMethodeResponse> methodes = e.getResultats().stream()
                .sorted(Comparator.comparing(ResultatMethode::getMethode))
                .map(ResultatMethodeResponse::depuis)
                .toList();
        return new EstimationResponse(e.getId(), bien.getId(), bien.nature(), e.getStatut(),
                e.getPrixMin(), e.getPrixCentral(), e.getPrixMax(), e.getIndiceConfiance(),
                e.getDateCreation(), e.getDemandePar(), avertissements, methodes);
    }
}
