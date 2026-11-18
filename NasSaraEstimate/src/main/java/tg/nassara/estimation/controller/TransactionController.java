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
import tg.nassara.estimation.dto.request.CreationTransactionRequest;
import tg.nassara.estimation.dto.request.ModificationTransactionRequest;
import tg.nassara.estimation.dto.response.PageResponse;
import tg.nassara.estimation.dto.response.TransactionResponse;
import tg.nassara.estimation.enums.StatutTransaction;
import tg.nassara.estimation.service.parametrage.Acteur;
import tg.nassara.estimation.service.transaction.TransactionService;

/** Transactions réelles de l'agence : saisie, contrôle et validation (source de l'apprentissage). */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<TransactionResponse> lister(@RequestParam(required = false) StatutTransaction statut,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int taille) {
        return service.lister(statut, page, taille);
    }

    @GetMapping("/{id}")
    public TransactionResponse obtenir(@PathVariable Long id) {
        return service.obtenir(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse creer(@Valid @RequestBody CreationTransactionRequest requete) {
        return service.creer(requete);
    }

    @PatchMapping("/{id}")
    public TransactionResponse modifier(@PathVariable Long id,
                                        @Valid @RequestBody ModificationTransactionRequest requete) {
        return service.modifier(id, requete);
    }

    @PostMapping("/{id}/conclusion")
    public TransactionResponse conclure(@PathVariable Long id) {
        return service.conclure(id);
    }

    @PostMapping("/{id}/validation")
    public TransactionResponse valider(@PathVariable Long id, Acteur acteur) {
        return service.valider(id, acteur);
    }

    @PostMapping("/{id}/annulation")
    public TransactionResponse annuler(@PathVariable Long id) {
        return service.annuler(id);
    }
}