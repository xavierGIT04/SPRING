package tg.nassara.estimation.service.transaction;

import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.nassara.estimation.dto.request.CreationTransactionRequest;
import tg.nassara.estimation.dto.request.ModificationTransactionRequest;
import tg.nassara.estimation.dto.response.PageResponse;
import tg.nassara.estimation.dto.response.TransactionResponse;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.entity.BienBati;
import tg.nassara.estimation.entity.Estimation;
import tg.nassara.estimation.entity.TransactionImmobiliere;
import tg.nassara.estimation.enums.StatutTransaction;
import tg.nassara.estimation.exception.ConflitException;
import tg.nassara.estimation.exception.DonneesInvalidesException;
import tg.nassara.estimation.exception.RessourceIntrouvableException;
import tg.nassara.estimation.repository.BienRepository;
import tg.nassara.estimation.repository.EstimationRepository;
import tg.nassara.estimation.repository.TransactionRepository;
import tg.nassara.estimation.service.parametrage.Acteur;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Saisie et validation des transactions réelles (vérité terrain de l'apprentissage).
 * Cycle : EN_COURS → CONCLUE → VALIDEE ; ANNULEE depuis EN_COURS, CONCLUE, ou VALIDEE non encore intégrée.
 */
@Service
public class TransactionService {

    private static final ZoneId FUSEAU = ZoneId.of("Africa/Lome");

    private final TransactionRepository transactionRepository;
    private final BienRepository bienRepository;
    private final EstimationRepository estimationRepository;

    public TransactionService(TransactionRepository transactionRepository, BienRepository bienRepository,
                              EstimationRepository estimationRepository) {
        this.transactionRepository = transactionRepository;
        this.bienRepository = bienRepository;
        this.estimationRepository = estimationRepository;
    }

    @Transactional
    public TransactionResponse creer(CreationTransactionRequest requete) {
        Bien bien = (Bien) Hibernate.unproxy(bienRepository.findById(requete.bienId())
                .orElseThrow(() -> new RessourceIntrouvableException("Bien introuvable : " + requete.bienId())));
        if (transactionRepository.existsByBienIdAndStatutNot(bien.getId(), StatutTransaction.ANNULEE)) {
            throw new ConflitException("Une transaction non annulée existe déjà pour ce bien : "
                    + "annulez-la d'abord, ou corrigez-la.");
        }
        if (requete.loyerMensuelConstate() != null) {
            if (!(bien instanceof BienBati bati) || !bati.isLocatifOuCommercial()) {
                throw new DonneesInvalidesException(
                        "Un loyer constaté n'a de sens que pour un bien bâti locatif ou commercial.");
            }
        }
        Estimation estimation = null;
        if (requete.estimationId() != null) {
            estimation = estimationRepository.findById(requete.estimationId())
                    .orElseThrow(() -> new RessourceIntrouvableException(
                            "Estimation introuvable : " + requete.estimationId()));
            if (!bien.getId().equals(estimation.getBien().getId())) {
                throw new DonneesInvalidesException("L'estimation préalable concerne un autre bien.");
            }
        }

        TransactionImmobiliere transaction = new TransactionImmobiliere();
        transaction.setBien(bien);
        transaction.setEstimationPrealable(estimation);
        transaction.setPrixVenteEffectif(requete.prixVenteEffectif());
        transaction.setLoyerMensuelConstate(requete.loyerMensuelConstate());
        transaction.setDateTransaction(requete.dateTransaction());
        transaction.setStatut(StatutTransaction.EN_COURS);
        return TransactionResponse.depuis(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse modifier(Long id, ModificationTransactionRequest requete) {
        TransactionImmobiliere transaction = charger(id);
        if (transaction.getStatut() != StatutTransaction.EN_COURS
                && transaction.getStatut() != StatutTransaction.CONCLUE) {
            throw new ConflitException("Seule une transaction en cours ou conclue est modifiable (statut : "
                    + transaction.getStatut() + ").");
        }
        if (requete.version() != null && !requete.version().equals(transaction.getVersion())) {
            throw new ConflitException("La transaction a été modifiée par quelqu'un d'autre. Rechargez-la puis réessayez.");
        }
        if (requete.prixVenteEffectif() == null && requete.loyerMensuelConstate() == null
                && requete.dateTransaction() == null) {
            throw new DonneesInvalidesException("Aucune valeur à modifier.");
        }
        if (requete.loyerMensuelConstate() != null) {
            Bien bien = (Bien) Hibernate.unproxy(transaction.getBien());
            if (!(bien instanceof BienBati bati) || !bati.isLocatifOuCommercial()) {
                throw new DonneesInvalidesException(
                        "Un loyer constaté n'a de sens que pour un bien bâti locatif ou commercial.");
            }
            transaction.setLoyerMensuelConstate(requete.loyerMensuelConstate());
        }
        if (requete.prixVenteEffectif() != null) {
            transaction.setPrixVenteEffectif(requete.prixVenteEffectif());
        }
        if (requete.dateTransaction() != null) {
            transaction.setDateTransaction(requete.dateTransaction());
        }
        return TransactionResponse.depuis(transactionRepository.save(transaction));
    }

    /** EN_COURS → CONCLUE : la vente est signée, mais pas encore contrôlée. */
    @Transactional
    public TransactionResponse conclure(Long id) {
        TransactionImmobiliere transaction = charger(id);
        exiger(transaction, StatutTransaction.EN_COURS, "conclure");
        transaction.setStatut(StatutTransaction.CONCLUE);
        return TransactionResponse.depuis(transactionRepository.save(transaction));
    }

    /** CONCLUE → VALIDEE : seule une transaction validée alimente la comparaison de marché et l'apprentissage. */
    @Transactional
    public TransactionResponse valider(Long id, Acteur acteur) {
        TransactionImmobiliere transaction = charger(id);
        exiger(transaction, StatutTransaction.CONCLUE, "valider");
        transaction.setStatut(StatutTransaction.VALIDEE);
        transaction.setValidePar(acteur.identifiant());
        transaction.setDateValidation(Instant.now());
        return TransactionResponse.depuis(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse annuler(Long id) {
        TransactionImmobiliere transaction = charger(id);
        if (transaction.getStatut() == StatutTransaction.ANNULEE) {
            throw new ConflitException("La transaction est déjà annulée.");
        }
        if (transaction.getStatut() == StatutTransaction.VALIDEE
                && (transaction.isIntegreApprentissage() || transaction.isIntegreCout())) {
            throw new ConflitException(
                    "Cette transaction a déjà servi au recalibrage : elle ne peut plus être annulée.");
        }
        transaction.setStatut(StatutTransaction.ANNULEE);
        return TransactionResponse.depuis(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public TransactionResponse obtenir(Long id) {
        return TransactionResponse.depuis(charger(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> lister(StatutTransaction statut, int page, int taille) {
        PageRequest pagination = PageRequest.of(Math.max(page, 0), Math.min(Math.max(taille, 1), 100));
        Page<TransactionImmobiliere> resultat = statut == null
                ? transactionRepository.findAllByOrderByDateTransactionDesc(pagination)
                : transactionRepository.findByStatutOrderByDateTransactionDesc(statut, pagination);
        return new PageResponse<>(resultat.getContent().stream().map(TransactionResponse::depuis).toList(),
                resultat.getNumber(), resultat.getSize(), resultat.getTotalElements(), resultat.getTotalPages());
    }

    private TransactionImmobiliere charger(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Transaction introuvable : " + id));
    }

    private void exiger(TransactionImmobiliere transaction, StatutTransaction attendu, String action) {
        if (transaction.getStatut() != attendu) {
            throw new ConflitException("Impossible de " + action + " : la transaction est " + transaction.getStatut()
                    + " (statut attendu : " + attendu + ").");
        }
    }
}