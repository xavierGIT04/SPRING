package tg.nassara.estimation.service.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import tg.nassara.estimation.dto.request.CreationTransactionRequest;
import tg.nassara.estimation.dto.request.ModificationTransactionRequest;
import tg.nassara.estimation.dto.response.TransactionResponse;
import tg.nassara.estimation.entity.BienBati;
import tg.nassara.estimation.entity.Estimation;
import tg.nassara.estimation.entity.TerrainNu;
import tg.nassara.estimation.entity.TransactionImmobiliere;
import tg.nassara.estimation.enums.StatutTransaction;
import tg.nassara.estimation.exception.ConflitException;
import tg.nassara.estimation.exception.DonneesInvalidesException;
import tg.nassara.estimation.exception.RessourceIntrouvableException;
import tg.nassara.estimation.repository.BienRepository;
import tg.nassara.estimation.repository.EstimationRepository;
import tg.nassara.estimation.repository.TransactionRepository;
import tg.nassara.estimation.service.parametrage.Acteur;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Tests unitaires du cycle de vie des transactions : tout est simulé (aucune base). */
class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private BienRepository bienRepository;
    private EstimationRepository estimationRepository;
    private TransactionService service;
    private TerrainNu terrain;

    @BeforeEach
    void preparer() {
        transactionRepository = mock(TransactionRepository.class);
        bienRepository = mock(BienRepository.class);
        estimationRepository = mock(EstimationRepository.class);
        service = new TransactionService(transactionRepository, bienRepository, estimationRepository);

        terrain = new TerrainNu();
        ReflectionTestUtils.setField(terrain, "id", 1L);
        when(bienRepository.findById(1L)).thenReturn(Optional.of(terrain));
        when(transactionRepository.save(any(TransactionImmobiliere.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void creer_valide_commenceEnCoursEtCalculeLEcartAvecLEstimation() {
        Estimation estimation = new Estimation();
        estimation.setBien(terrain);
        estimation.setPrixCentral(bd("50000000"));
        ReflectionTestUtils.setField(estimation, "id", 7L);
        when(estimationRepository.findById(7L)).thenReturn(Optional.of(estimation));

        TransactionResponse reponse = service.creer(
                new CreationTransactionRequest(1L, 7L, bd("45000000"), null, LocalDate.now()));

        assertThat(reponse.statut()).isEqualTo(StatutTransaction.EN_COURS);
        assertThat(reponse.integreApprentissage()).isFalse();
        assertThat(reponse.estimationId()).isEqualTo(7L);
        assertThat(reponse.ecartRelatif()).isEqualByComparingTo("-0.1000"); // (45 − 50) ÷ 50
    }

    @Test
    void creer_bienInconnu_leveRessourceIntrouvable() {
        assertThatThrownBy(() -> service.creer(
                new CreationTransactionRequest(99L, null, bd("1000"), null, LocalDate.now())))
                .isInstanceOf(RessourceIntrouvableException.class);
    }

    @Test
    void creer_doublon_estRefuse() {
        when(transactionRepository.existsByBienIdAndStatutNot(1L, StatutTransaction.ANNULEE)).thenReturn(true);

        assertThatThrownBy(() -> service.creer(
                new CreationTransactionRequest(1L, null, bd("1000"), null, LocalDate.now())))
                .isInstanceOf(ConflitException.class);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void creer_loyerSurUnTerrain_estRefuse() {
        assertThatThrownBy(() -> service.creer(
                new CreationTransactionRequest(1L, null, bd("1000"), bd("100"), LocalDate.now())))
                .isInstanceOf(DonneesInvalidesException.class)
                .hasMessageContaining("loyer");
    }

    @Test
    void creer_loyerSurBatiNonLocatif_estRefuse() {
        BienBati bati = new BienBati();
        ReflectionTestUtils.setField(bati, "id", 2L);
        when(bienRepository.findById(2L)).thenReturn(Optional.of(bati));

        assertThatThrownBy(() -> service.creer(
                new CreationTransactionRequest(2L, null, bd("1000"), bd("100"), LocalDate.now())))
                .isInstanceOf(DonneesInvalidesException.class);
    }

    @Test
    void creer_estimationDUnAutreBien_estRefusee() {
        TerrainNu autre = new TerrainNu();
        ReflectionTestUtils.setField(autre, "id", 2L);
        Estimation estimation = new Estimation();
        estimation.setBien(autre);
        when(estimationRepository.findById(7L)).thenReturn(Optional.of(estimation));

        assertThatThrownBy(() -> service.creer(
                new CreationTransactionRequest(1L, 7L, bd("1000"), null, LocalDate.now())))
                .isInstanceOf(DonneesInvalidesException.class)
                .hasMessageContaining("autre bien");
    }

    @Test
    void cycleComplet_conclurePuisValider_tracesLAuteur() {
        TransactionImmobiliere transaction = transaction(StatutTransaction.EN_COURS);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(transaction));

        service.conclure(5L);
        TransactionResponse reponse = service.valider(5L, Acteur.agent("agent.test"));

        assertThat(reponse.statut()).isEqualTo(StatutTransaction.VALIDEE);
        assertThat(reponse.validePar()).isEqualTo("agent.test");
        assertThat(reponse.dateValidation()).isNotNull();
    }

    @Test
    void valider_depuisEnCours_estRefuse() {
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(transaction(StatutTransaction.EN_COURS)));

        assertThatThrownBy(() -> service.valider(5L, Acteur.agent("agent.test")))
                .isInstanceOf(ConflitException.class);
    }

    @Test
    void modifier_transactionValidee_estRefusee() {
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(transaction(StatutTransaction.VALIDEE)));

        assertThatThrownBy(() -> service.modifier(5L,
                new ModificationTransactionRequest(null, bd("2000"), null, null)))
                .isInstanceOf(ConflitException.class);
    }

    @Test
    void modifier_versionPeriminee_estRefusee() {
        TransactionImmobiliere transaction = transaction(StatutTransaction.EN_COURS);
        ReflectionTestUtils.setField(transaction, "version", 3L);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(transaction));

        assertThatThrownBy(() -> service.modifier(5L,
                new ModificationTransactionRequest(2L, bd("2000"), null, null)))
                .isInstanceOf(ConflitException.class);
    }

    @Test
    void annuler_valideeNonIntegree_estAutorisee_integree_estRefusee() {
        TransactionImmobiliere libre = transaction(StatutTransaction.VALIDEE);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(libre));
        assertThat(service.annuler(5L).statut()).isEqualTo(StatutTransaction.ANNULEE);

        TransactionImmobiliere consommee = transaction(StatutTransaction.VALIDEE);
        consommee.setIntegreApprentissage(true);
        when(transactionRepository.findById(6L)).thenReturn(Optional.of(consommee));
        assertThatThrownBy(() -> service.annuler(6L)).isInstanceOf(ConflitException.class);
    }

    // ---------- Fabriques ----------

    private TransactionImmobiliere transaction(StatutTransaction statut) {
        TransactionImmobiliere transaction = new TransactionImmobiliere();
        transaction.setBien(terrain);
        transaction.setPrixVenteEffectif(bd("45000000"));
        transaction.setDateTransaction(LocalDate.now());
        transaction.setStatut(statut);
        return transaction;
    }

    private static BigDecimal bd(String valeur) {
        return new BigDecimal(valeur);
    }
}