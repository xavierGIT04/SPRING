package tg.nassara.estimation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tg.nassara.estimation.dto.response.EstimationResponse;
import tg.nassara.estimation.dto.response.EstimationResumeResponse;
import tg.nassara.estimation.service.estimation.ConsultationEstimationService;
import tg.nassara.estimation.service.estimation.ServiceEstimation;
import tg.nassara.estimation.service.parametrage.Acteur;

import java.util.List;

/** Lancement et consultation des estimations. L'auteur de la demande est résolu par ConfigurationWeb. */
@RestController
@RequestMapping("/api")
public class EstimationController {

    private final ServiceEstimation serviceEstimation;
    private final ConsultationEstimationService consultation;

    public EstimationController(ServiceEstimation serviceEstimation, ConsultationEstimationService consultation) {
        this.serviceEstimation = serviceEstimation;
        this.consultation = consultation;
    }

    /** Lance (ou relance) l'estimation d'un bien déjà enregistré. */
    @PostMapping("/biens/{bienId}/estimations")
    @ResponseStatus(HttpStatus.CREATED)
    public EstimationResponse estimer(@PathVariable Long bienId, Acteur acteur) {
        return serviceEstimation.estimer(bienId, acteur);
    }

    @GetMapping("/biens/{bienId}/estimations")
    public List<EstimationResumeResponse> historique(@PathVariable Long bienId) {
        return consultation.listerParBien(bienId);
    }

    @GetMapping("/estimations/{id}")
    public EstimationResponse obtenir(@PathVariable Long id) {
        return consultation.obtenir(id);
    }
}
