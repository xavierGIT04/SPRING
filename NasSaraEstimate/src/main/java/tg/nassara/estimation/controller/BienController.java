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
import tg.nassara.estimation.dto.request.CreationBienRequest;
import tg.nassara.estimation.dto.response.BienResponse;
import tg.nassara.estimation.dto.response.BienResume;
import tg.nassara.estimation.dto.response.PageResponse;
import tg.nassara.estimation.service.bien.BienService;

/** Saisie des biens à estimer (terrain nu ou bâti) par les agents. */
@RestController
@RequestMapping("/api/biens")
public class BienController {

    private final BienService service;

    public BienController(BienService service) {
        this.service = service;
    }

    /** Liste paginée, du bien le plus récent au plus ancien (étape 8 bis). */
    @GetMapping
    public PageResponse<BienResume> lister(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int taille) {
        return service.lister(page, taille);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BienResponse creer(@Valid @RequestBody CreationBienRequest requete) {
        return service.creer(requete);
    }

    @GetMapping("/{id}")
    public BienResponse obtenir(@PathVariable Long id) {
        return service.obtenir(id);
    }
}
