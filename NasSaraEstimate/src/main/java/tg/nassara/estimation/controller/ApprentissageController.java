package tg.nassara.estimation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tg.nassara.estimation.dto.response.RecalibrageResponse;
import tg.nassara.estimation.service.apprentissage.RecalibrageService;
import tg.nassara.estimation.service.parametrage.Acteur;

/** Déclenchement manuel de l'apprentissage continu (le bouton « Recalibrer » du CRM). */
@RestController
@RequestMapping("/api/apprentissage")
public class ApprentissageController {

    private final RecalibrageService service;

    public ApprentissageController(RecalibrageService service) {
        this.service = service;
    }

    /** « simulation=true » : calcule et renvoie le bilan sans rien écrire. */
    @PostMapping("/recalibrage")
    public RecalibrageResponse recalibrer(@RequestParam(defaultValue = "false") boolean simulation, Acteur acteur) {
        return service.recalibrer(simulation, acteur);
    }
}