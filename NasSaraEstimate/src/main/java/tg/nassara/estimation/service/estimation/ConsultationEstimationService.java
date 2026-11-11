package tg.nassara.estimation.service.estimation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.nassara.estimation.dto.response.EstimationResponse;
import tg.nassara.estimation.dto.response.EstimationResumeResponse;
import tg.nassara.estimation.exception.RessourceIntrouvableException;
import tg.nassara.estimation.repository.BienRepository;
import tg.nassara.estimation.repository.EstimationRepository;

import java.util.List;

/** Relecture des estimations enregistrées. */
@Service
@Transactional(readOnly = true)
public class ConsultationEstimationService {

    private final EstimationRepository estimationRepository;
    private final BienRepository bienRepository;

    public ConsultationEstimationService(EstimationRepository estimationRepository, BienRepository bienRepository) {
        this.estimationRepository = estimationRepository;
        this.bienRepository = bienRepository;
    }

    public EstimationResponse obtenir(Long id) {
        return EstimationResponse.depuis(estimationRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Estimation introuvable : " + id)));
    }

    /** Historique d'un bien, de la plus récente à la plus ancienne. */
    public List<EstimationResumeResponse> listerParBien(Long bienId) {
        if (!bienRepository.existsById(bienId)) {
            throw new RessourceIntrouvableException("Bien introuvable : " + bienId);
        }
        return estimationRepository.findByBienIdOrderByDateCreationDesc(bienId).stream()
                .map(EstimationResumeResponse::depuis)
                .toList();
    }
}
