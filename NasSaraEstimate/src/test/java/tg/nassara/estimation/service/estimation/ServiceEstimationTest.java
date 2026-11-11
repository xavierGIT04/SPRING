package tg.nassara.estimation.service.estimation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.PlatformTransactionManager;
import tg.nassara.estimation.dto.response.EstimationResponse;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.entity.BienBati;
import tg.nassara.estimation.entity.Estimation;
import tg.nassara.estimation.entity.PonderationMethode;
import tg.nassara.estimation.entity.TerrainNu;
import tg.nassara.estimation.enums.MethodeEstimation;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.StatutEstimation;
import tg.nassara.estimation.exception.DonneesInsuffisantesException;
import tg.nassara.estimation.exception.EstimationImpossibleException;
import tg.nassara.estimation.exception.ParametreManquantException;
import tg.nassara.estimation.exception.RessourceIntrouvableException;
import tg.nassara.estimation.repository.BienRepository;
import tg.nassara.estimation.repository.EstimationRepository;
import tg.nassara.estimation.service.marche.ResultatSynchronisation;
import tg.nassara.estimation.service.marche.ServiceSynchronisationCatalogue;
import tg.nassara.estimation.service.parametrage.Acteur;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/** Tests unitaires de l'orchestrateur : tout est simulé (aucune base, aucun réseau). */
class ServiceEstimationTest {

    private final Acteur acteur = Acteur.agent("agent.test");

    private BienRepository bienRepository;
    private EstimationRepository estimationRepository;
    private ParametresCalcul parametres;
    private RegistreEstimateurs registre;
    private ServiceSynchronisationCatalogue synchronisation;
    private PlatformTransactionManager transactionManager;

    @BeforeEach
    void preparer() {
        bienRepository = mock(BienRepository.class);
        estimationRepository = mock(EstimationRepository.class);
        parametres = mock(ParametresCalcul.class);
        registre = mock(RegistreEstimateurs.class);
        synchronisation = mock(ServiceSynchronisationCatalogue.class);
        transactionManager = mock(PlatformTransactionManager.class);

        when(bienRepository.existsById(1L)).thenReturn(true);
        when(estimationRepository.save(any(Estimation.class))).thenAnswer(i -> i.getArgument(0));
        when(parametres.valeur(ClesParametres.FUSION_ECART_ALERTE_METHODES)).thenReturn(bd("0.30"));
        when(parametres.valeur(ClesParametres.FUSION_CONFIANCE_FACTEUR_METHODE_UNIQUE)).thenReturn(bd("0.80"));
        when(synchronisation.synchroniserSiNecessaire()).thenReturn(synchro(
                ResultatSynchronisation.Statut.ACTUALISEE, "Catalogue actualisé."));
    }

    @Test
    void deuxMethodes_fusionnentEtEnregistrent() {
        bien(new TerrainNu());
        ponderations(NatureBien.TERRAIN,
                pond(MethodeEstimation.HEDONIQUE, NatureBien.TERRAIN, "1"),
                pond(MethodeEstimation.COMPARAISON_MARCHE, NatureBien.TERRAIN, "1"));
        estimateur(MethodeEstimation.HEDONIQUE, true,
                resultat(MethodeEstimation.HEDONIQUE, "72000000", "80000000", "88000000", "0.60"));
        estimateur(MethodeEstimation.COMPARAISON_MARCHE, true,
                resultat(MethodeEstimation.COMPARAISON_MARCHE, "36000000", "40000000", "44000000", "0.20"));

        EstimationResponse reponse = service().estimer(1L, acteur);

        // Mêmes chiffres que FusionResultatsTest : parts 0,75 / 0,25.
        assertThat(reponse.statut()).isEqualTo(StatutEstimation.CALCULEE);
        assertThat(reponse.prixCentral()).isEqualByComparingTo("70000000");
        assertThat(reponse.prixMin()).isEqualByComparingTo("63000000");
        assertThat(reponse.prixMax()).isEqualByComparingTo("77000000");
        assertThat(reponse.indiceConfiance()).isEqualByComparingTo("0.50");
        assertThat(reponse.demandePar()).isEqualTo("agent.test");
        assertThat(reponse.methodes()).hasSize(2);
        assertThat(reponse.avertissements()).anyMatch(a -> a.contains("Écart"));

        ArgumentCaptor<Estimation> capteur = ArgumentCaptor.forClass(Estimation.class);
        verify(estimationRepository).save(capteur.capture());
        Estimation enregistree = capteur.getValue();
        assertThat(enregistree.getResultats()).hasSize(2);
        assertThat(enregistree.getResultats().get(0).getMethode()).isEqualTo(MethodeEstimation.HEDONIQUE);
        assertThat(enregistree.getResultats().get(0).getPoidsApplique()).isEqualByComparingTo("0.75");
        assertThat(enregistree.getResultats().get(0).getDetailCalcul())
                .contains("\"poidsParametre\"").contains("\"partFusion\"").contains("\"calcul\"");
    }

    @Test
    void methodeEcartee_rendLEstimationPartielle() {
        bien(new TerrainNu());
        ponderations(NatureBien.TERRAIN,
                pond(MethodeEstimation.HEDONIQUE, NatureBien.TERRAIN, "1"),
                pond(MethodeEstimation.COMPARAISON_MARCHE, NatureBien.TERRAIN, "1"));
        estimateur(MethodeEstimation.HEDONIQUE, true,
                resultat(MethodeEstimation.HEDONIQUE, "72000000", "80000000", "88000000", "0.60"));
        estimateurInsuffisant(MethodeEstimation.COMPARAISON_MARCHE);

        EstimationResponse reponse = service().estimer(1L, acteur);

        assertThat(reponse.statut()).isEqualTo(StatutEstimation.PARTIELLE);
        assertThat(reponse.prixCentral()).isEqualByComparingTo("80000000");
        // 0,60 × 0,80 (méthode unique) = 0,48
        assertThat(reponse.indiceConfiance()).isEqualByComparingTo("0.48");
        assertThat(reponse.avertissements()).anyMatch(a -> a.contains("Méthode écartée"));
        assertThat(reponse.avertissements()).anyMatch(a -> a.contains("recoupement"));
    }

    @Test
    void methodeNonApplicable_neRendPasPartielle() {
        bien(new BienBati());
        ponderations(NatureBien.BATI,
                pond(MethodeEstimation.HEDONIQUE, NatureBien.BATI, "1"),
                pond(MethodeEstimation.CAPITALISATION, NatureBien.BATI, "1"));
        estimateur(MethodeEstimation.HEDONIQUE, true,
                resultat(MethodeEstimation.HEDONIQUE, "72000000", "80000000", "88000000", "0.60"));
        estimateur(MethodeEstimation.CAPITALISATION, false,
                resultat(MethodeEstimation.CAPITALISATION, "1", "2", "3", "0.50"));

        EstimationResponse reponse = service().estimer(1L, acteur);

        assertThat(reponse.statut()).isEqualTo(StatutEstimation.CALCULEE);
        assertThat(reponse.methodes()).hasSize(1);
        assertThat(reponse.avertissements()).anyMatch(a -> a.contains("non applicable"));
    }

    @Test
    void aucunePonderationActive_leveParametreManquant() {
        bien(new TerrainNu());
        ponderations(NatureBien.TERRAIN);

        assertThatThrownBy(() -> service().estimer(1L, acteur))
                .isInstanceOf(ParametreManquantException.class)
                .hasMessageContaining("PonderationMethode");
        verify(estimationRepository, never()).save(any());
    }

    @Test
    void toutesMethodesEcartees_leveEstimationImpossible() {
        bien(new TerrainNu());
        ponderations(NatureBien.TERRAIN,
                pond(MethodeEstimation.HEDONIQUE, NatureBien.TERRAIN, "1"),
                pond(MethodeEstimation.COMPARAISON_MARCHE, NatureBien.TERRAIN, "1"));
        estimateurInsuffisant(MethodeEstimation.HEDONIQUE);
        estimateurInsuffisant(MethodeEstimation.COMPARAISON_MARCHE);

        assertThatThrownBy(() -> service().estimer(1L, acteur))
                .isInstanceOfSatisfying(EstimationImpossibleException.class,
                        e -> assertThat(e.getRaisons()).hasSize(2));
        verify(estimationRepository, never()).save(any());
    }

    @Test
    void parametreManquantDansUneMethode_arreteToutSansEnregistrer() {
        bien(new TerrainNu());
        ponderations(NatureBien.TERRAIN, pond(MethodeEstimation.HEDONIQUE, NatureBien.TERRAIN, "1"));
        Estimateur estimateur = mock(Estimateur.class);
        when(estimateur.methode()).thenReturn(MethodeEstimation.HEDONIQUE);
        when(estimateur.estApplicable(any())).thenReturn(true);
        when(estimateur.estimer(any())).thenThrow(new ParametreManquantException("ParametreGlobal", "X"));
        when(registre.pour(MethodeEstimation.HEDONIQUE)).thenReturn(Optional.of(estimateur));

        assertThatThrownBy(() -> service().estimer(1L, acteur)).isInstanceOf(ParametreManquantException.class);
        verify(estimationRepository, never()).save(any());
    }

    @Test
    void bienIntrouvable_leveRessourceIntrouvableSansLireLeCatalogue() {
        assertThatThrownBy(() -> service().estimer(99L, acteur)).isInstanceOf(RessourceIntrouvableException.class);
        verifyNoInteractions(synchronisation);
    }

    @Test
    void catalogueNonActualise_estRelayeDansLesAvertissements() {
        bien(new TerrainNu());
        when(synchronisation.synchroniserSiNecessaire()).thenReturn(synchro(
                ResultatSynchronisation.Statut.ECHEC, "Catalogue non actualisé (site injoignable)."));
        ponderations(NatureBien.TERRAIN, pond(MethodeEstimation.HEDONIQUE, NatureBien.TERRAIN, "1"));
        estimateur(MethodeEstimation.HEDONIQUE, true,
                resultat(MethodeEstimation.HEDONIQUE, "72000000", "80000000", "88000000", "0.60"));

        EstimationResponse reponse = service().estimer(1L, acteur);

        assertThat(reponse.avertissements()).anyMatch(a -> a.contains("Catalogue non actualisé"));
    }

    // ---------- Fabriques ----------

    private ServiceEstimation service() {
        return new ServiceEstimation(bienRepository, estimationRepository, parametres, registre,
                new FusionResultats(parametres), synchronisation, transactionManager);
    }

    private void bien(Bien bien) {
        when(bienRepository.findById(1L)).thenReturn(Optional.of(bien));
    }

    private void ponderations(NatureBien nature, PonderationMethode... ponderations) {
        when(parametres.ponderationsActives(nature)).thenReturn(List.of(ponderations));
    }

    private static PonderationMethode pond(MethodeEstimation methode, NatureBien nature, String poids) {
        return new PonderationMethode(methode, nature, bd(poids));
    }

    private void estimateur(MethodeEstimation methode, boolean applicable, ResultatMethodeCalcule resultat) {
        Estimateur estimateur = mock(Estimateur.class);
        when(estimateur.methode()).thenReturn(methode);
        when(estimateur.estApplicable(any())).thenReturn(applicable);
        when(estimateur.estimer(any())).thenReturn(resultat);
        when(registre.pour(methode)).thenReturn(Optional.of(estimateur));
    }

    private void estimateurInsuffisant(MethodeEstimation methode) {
        Estimateur estimateur = mock(Estimateur.class);
        when(estimateur.methode()).thenReturn(methode);
        when(estimateur.estApplicable(any())).thenReturn(true);
        when(estimateur.estimer(any())).thenThrow(
                new DonneesInsuffisantesException(methode, "la recherche n'a pas été concluante."));
        when(registre.pour(methode)).thenReturn(Optional.of(estimateur));
    }

    private static ResultatMethodeCalcule resultat(MethodeEstimation methode, String min, String central,
                                                   String max, String confiance) {
        return new ResultatMethodeCalcule(methode, bd(min), bd(central), bd(max), bd(confiance),
                Map.of("cle", "valeur"), List.of());
    }

    private static ResultatSynchronisation synchro(ResultatSynchronisation.Statut statut, String message) {
        return new ResultatSynchronisation(statut, 0, 0, 0, Instant.now(), message);
    }

    private static BigDecimal bd(String valeur) {
        return new BigDecimal(valeur);
    }
}
