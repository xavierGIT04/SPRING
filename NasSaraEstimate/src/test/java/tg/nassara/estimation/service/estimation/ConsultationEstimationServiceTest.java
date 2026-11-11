package tg.nassara.estimation.service.estimation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tg.nassara.estimation.dto.response.EstimationResponse;
import tg.nassara.estimation.dto.response.EstimationResumeResponse;
import tg.nassara.estimation.entity.Estimation;
import tg.nassara.estimation.entity.ResultatMethode;
import tg.nassara.estimation.entity.TerrainNu;
import tg.nassara.estimation.enums.MethodeEstimation;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.StatutEstimation;
import tg.nassara.estimation.exception.RessourceIntrouvableException;
import tg.nassara.estimation.repository.BienRepository;
import tg.nassara.estimation.repository.EstimationRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConsultationEstimationServiceTest {

    private EstimationRepository estimationRepository;
    private BienRepository bienRepository;
    private ConsultationEstimationService service;

    @BeforeEach
    void preparer() {
        estimationRepository = mock(EstimationRepository.class);
        bienRepository = mock(BienRepository.class);
        service = new ConsultationEstimationService(estimationRepository, bienRepository);
    }

    @Test
    void obtenir_restitueLesAvertissementsEtLeDetailJsonTelQuEnregistre() {
        Estimation estimation = estimation();
        when(estimationRepository.findById(5L)).thenReturn(Optional.of(estimation));

        EstimationResponse reponse = service.obtenir(5L);

        assertThat(reponse.nature()).isEqualTo(NatureBien.TERRAIN);
        assertThat(reponse.statut()).isEqualTo(StatutEstimation.PARTIELLE);
        assertThat(reponse.prixCentral()).isEqualByComparingTo("70000000");
        assertThat(reponse.avertissements()).containsExactly("premier", "second");
        assertThat(reponse.methodes()).hasSize(1);
        assertThat(reponse.methodes().get(0).detail()).isEqualTo("{\"calcul\":{}}");
        assertThat(reponse.methodes().get(0).partFusion()).isEqualByComparingTo("1");
    }

    @Test
    void obtenir_estimationInconnue_leveRessourceIntrouvable() {
        assertThatThrownBy(() -> service.obtenir(404L)).isInstanceOf(RessourceIntrouvableException.class);
    }

    @Test
    void listerParBien_bienInconnu_leveRessourceIntrouvable() {
        assertThatThrownBy(() -> service.listerParBien(404L)).isInstanceOf(RessourceIntrouvableException.class);
    }

    @Test
    void listerParBien_renvoieLHistorique() {
        when(bienRepository.existsById(1L)).thenReturn(true);
        when(estimationRepository.findByBienIdOrderByDateCreationDesc(1L)).thenReturn(List.of(estimation()));

        List<EstimationResumeResponse> historique = service.listerParBien(1L);

        assertThat(historique).hasSize(1);
        assertThat(historique.get(0).prixCentral()).isEqualByComparingTo("70000000");
    }

    private static Estimation estimation() {
        Estimation estimation = new Estimation();
        estimation.setBien(new TerrainNu());
        estimation.setPrixMin(new BigDecimal("63000000"));
        estimation.setPrixCentral(new BigDecimal("70000000"));
        estimation.setPrixMax(new BigDecimal("77000000"));
        estimation.setIndiceConfiance(new BigDecimal("0.5"));
        estimation.setStatut(StatutEstimation.PARTIELLE);
        estimation.setDemandePar("agent.test");
        estimation.setAvertissements("premier\nsecond");

        ResultatMethode resultat = new ResultatMethode();
        resultat.setMethode(MethodeEstimation.HEDONIQUE);
        resultat.setPrixMin(new BigDecimal("63000000"));
        resultat.setPrixCentral(new BigDecimal("70000000"));
        resultat.setPrixMax(new BigDecimal("77000000"));
        resultat.setIndiceConfiance(new BigDecimal("0.5"));
        resultat.setPoidsApplique(BigDecimal.ONE);
        resultat.setDetailCalcul("{\"calcul\":{}}");
        estimation.ajouterResultat(resultat);
        return estimation;
    }
}
