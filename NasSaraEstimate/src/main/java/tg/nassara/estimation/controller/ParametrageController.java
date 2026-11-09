package tg.nassara.estimation.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tg.nassara.estimation.dto.request.CreationParametreRequest;
import tg.nassara.estimation.dto.response.HistoriqueParametreResponse;
import tg.nassara.estimation.dto.request.ModificationParametreRequest;
import tg.nassara.estimation.dto.response.PageResponse;
import tg.nassara.estimation.dto.response.ParametreResponse;
import tg.nassara.estimation.dto.request.RestaurationRequest;
import tg.nassara.estimation.dto.response.TypeParametreResponse;
import tg.nassara.estimation.service.parametrage.Acteur;
import tg.nassara.estimation.service.parametrage.ParametrageService;

import java.util.List;

/** Administration de tous les paramètres de calcul. L'auteur des écritures est résolu par ConfigurationWeb. */
@RestController
@RequestMapping("/api/parametres")
public class ParametrageController {

    private final ParametrageService service;

    public ParametrageController(ParametrageService service) {
        this.service = service;
    }

    @GetMapping
    public List<TypeParametreResponse> types() {
        return service.types();
    }

    @GetMapping("/historique")
    public PageResponse<HistoriqueParametreResponse> historiqueGlobal(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int taille) {
        return service.historiqueGlobal(page, taille);
    }

    @PostMapping("/historique/{historiqueId}/restauration")
    public ParametreResponse restaurer(@PathVariable Long historiqueId,
                                       @Valid @RequestBody RestaurationRequest requete,
                                       Acteur acteur) {
        return service.restaurer(historiqueId, requete.motif(), acteur);
    }

    @GetMapping("/{type}")
    public List<ParametreResponse> lister(@PathVariable String type,
                                          @RequestParam(defaultValue = "false") boolean actifsSeulement) {
        return service.lister(type, actifsSeulement);
    }

    @GetMapping("/{type}/{id}")
    public ParametreResponse obtenir(@PathVariable String type, @PathVariable Long id) {
        return service.obtenir(type, id);
    }

    @PostMapping("/{type}")
    @ResponseStatus(HttpStatus.CREATED)
    public ParametreResponse creer(@PathVariable String type,
                                   @Valid @RequestBody CreationParametreRequest requete,
                                   Acteur acteur) {
        return service.creer(type, requete.valeurs(), requete.motif(), acteur);
    }

    @PatchMapping("/{type}/{id}")
    public ParametreResponse modifier(@PathVariable String type, @PathVariable Long id,
                                      @Valid @RequestBody ModificationParametreRequest requete,
                                      Acteur acteur) {
        return service.modifier(type, id, requete.version(), requete.valeurs(), requete.motif(), acteur);
    }

    @GetMapping("/{type}/{id}/historique")
    public PageResponse<HistoriqueParametreResponse> historique(@PathVariable String type, @PathVariable Long id,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int taille) {
        return service.historique(type, id, page, taille);
    }
}