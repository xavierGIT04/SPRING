package tg.nassara.estimation.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tg.nassara.estimation.dto.request.CreationQuartierRequest;
import tg.nassara.estimation.dto.response.ParametreResponse;
import tg.nassara.estimation.dto.response.QuartierResume;
import tg.nassara.estimation.service.parametrage.Acteur;
import tg.nassara.estimation.service.parametrage.QuartierService;

import java.util.List;

/** Parcours rapide : retrouver un quartier, ou le créer s'il est inconnu. */
@RestController
@RequestMapping("/api/quartiers")
public class QuartierController {


    private final QuartierService service;

    public QuartierController(QuartierService service) {
        this.service = service;
    }

    @GetMapping
    public List<QuartierResume> rechercher(@RequestParam(name = "recherche", defaultValue = "") String recherche) {
        return service.rechercher(recherche);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParametreResponse creer(@Valid @RequestBody CreationQuartierRequest requete, Acteur acteur) {
        return service.creerRapidement(requete, Acteur.class.cast(acteur));
    }
}