package tg.nassara.estimation.service.parametrage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tg.nassara.estimation.dto.response.HistoriqueParametreResponse;
import tg.nassara.estimation.dto.response.PageResponse;
import tg.nassara.estimation.entity.HistoriqueParametre;
import tg.nassara.estimation.enums.TypeOperation;
import tg.nassara.estimation.exception.RessourceIntrouvableException;
import tg.nassara.estimation.repository.HistoriqueParametreRepository;

@Service
public class HistoriqueParametreService {

    private static final int TAILLE_MAX_PAGE = 100;

    private final HistoriqueParametreRepository repository;

    public HistoriqueParametreService(HistoriqueParametreRepository repository) {
        this.repository = repository;
    }

    /** Doit être appelé dans la transaction qui modifie le paramètre : tout réussit ou tout échoue ensemble. */
    @Transactional(propagation = Propagation.MANDATORY)
    public HistoriqueParametre enregistrer(String typeParametre, Long idParametre, String reference,
                                           TypeOperation operation, String champ,
                                           String ancienneValeur, String nouvelleValeur,
                                           Acteur acteur, String motif, Long restaureDepuisId) {
        HistoriqueParametre ligne = new HistoriqueParametre(typeParametre, idParametre, reference, operation,
                champ, ancienneValeur, nouvelleValeur, acteur.origine(), acteur.identifiant(),
                motif, restaureDepuisId);
        return repository.save(ligne);
    }

    @Transactional(readOnly = true)
    public HistoriqueParametre trouver(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Ligne d'historique introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public PageResponse<HistoriqueParametreResponse> historiqueDe(String typeParametre, Long idParametre,
                                                                  int page, int taille) {
        return versPage(repository.findByTypeParametreAndIdParametreOrderByDateModificationDesc(
                typeParametre, idParametre, pagination(page, taille)));
    }

    @Transactional(readOnly = true)
    public PageResponse<HistoriqueParametreResponse> historiqueGlobal(int page, int taille) {
        return versPage(repository.findAllByOrderByDateModificationDesc(pagination(page, taille)));
    }

    private PageRequest pagination(int page, int taille) {
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(taille, 1), TAILLE_MAX_PAGE));
    }

    private PageResponse<HistoriqueParametreResponse> versPage(Page<HistoriqueParametre> resultat) {
        return new PageResponse<>(
                resultat.getContent().stream().map(HistoriqueParametreResponse::depuis).toList(),
                resultat.getNumber(), resultat.getSize(), resultat.getTotalElements(), resultat.getTotalPages());
    }
}