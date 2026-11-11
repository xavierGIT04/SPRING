package tg.nassara.estimation.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tg.nassara.estimation.dto.request.CreationAnnonceMarcheRequest;
import tg.nassara.estimation.dto.response.AnnonceMarcheResponse;
import tg.nassara.estimation.service.marche.AnnonceMarcheService;
import tg.nassara.estimation.service.marche.ResultatSynchronisation;
import tg.nassara.estimation.service.marche.ServiceSynchronisationCatalogue;
import tg.nassara.estimation.service.parametrage.Acteur;

import java.util.List;

/** Annonces de marché : saisie manuelle par les agents et actualisation du catalogue de l'agence. */
@RestController
@RequestMapping("/api/annonces-marche")
public class AnnonceMarcheController {

    private final AnnonceMarcheService service;
    private final ServiceSynchronisationCatalogue synchronisation;

    public AnnonceMarcheController(AnnonceMarcheService service, ServiceSynchronisationCatalogue synchronisation) {
        this.service = service;
        this.synchronisation = synchronisation;
    }

    @GetMapping
    public List<AnnonceMarcheResponse> lister(@RequestParam(required = false) Long quartierId,
                                              @RequestParam(defaultValue = "true") boolean actifsSeulement) {
        return service.lister(quartierId, actifsSeulement);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnnonceMarcheResponse creer(@Valid @RequestBody CreationAnnonceMarcheRequest requete, Acteur acteur) {
        return service.creer(requete, acteur);
    }

    @PostMapping("/{id}/desactivation")
    public AnnonceMarcheResponse desactiver(@PathVariable Long id) {
        return service.desactiver(id);
    }

    @PostMapping("/catalogue/synchronisation")
    public ResultatSynchronisation synchroniser() {
        return synchronisation.forcer();
    }
}
