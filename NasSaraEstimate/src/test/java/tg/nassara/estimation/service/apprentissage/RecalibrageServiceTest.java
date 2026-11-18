package tg.nassara.estimation.service.apprentissage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import tg.nassara.estimation.dto.response.RecalibrageResponse;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.entity.BienBati;
import tg.nassara.estimation.entity.Equipement;
import tg.nassara.estimation.entity.TerrainNu;
import tg.nassara.estimation.entity.TransactionImmobiliere;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.entity.reference.TypeEquipement;
import tg.nassara.estimation.enums.MethodeEstimation;
import tg.nassara.estimation.enums.StatutTransaction;
import tg.nassara.estimation.exception.ParametreManquantException;
import tg.nassara.estimation.repository.QuartierRepository;
import tg.nassara.estimation.repository.TransactionRepository;
import tg.nassara.estimation.service.estimation.ClesParametres;
import tg.nassara.estimation.service.estimation.EstimateurHedonique;
import tg.nassara.estimation.service.estimation.ParametresCalcul;
import tg.nassara.estimation.service.estimation.ResultatMethodeCalcule;
import tg.nassara.estimation.service.parametrage.Acteur;
import tg.nassara.estimation.service.parametrage.ParametrageService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.isNull;

/** Tests unitaires du recalibrage : tout est simulé (aucune base). Chiffres calculés à la main. */
class RecalibrageServiceTest {

    private final Acteur acteur = Acteur.agent("agent.test");

    private TransactionRepository transactionRepository;
    private QuartierRepository quartierRepository;
    private EstimateurHedonique hedonique;
    private ParametresCalcul parametres;
    private ParametrageService parametrageService;
    private RecalibrageService service;
    private Quartier quartier;
    private long sequence = 100;

    @BeforeEach
    void preparer() {
        transactionRepository = mock(TransactionRepository.class);
        quartierRepository = mock(QuartierRepository.class);
        hedonique = mock(EstimateurHedonique.class);
        parametres = mock(ParametresCalcul.class);
        parametrageService = mock(ParametrageService.class);

        quartier = new Quartier("TOKOIN", "Tokoin", bd("100000"));
        ReflectionTestUtils.setField(quartier, "id", 10L);
        when(quartierRepository.findById(10L)).thenReturn(Optional.of(quartier));

        when(parametres.valeur(ClesParametres.APPRENTISSAGE_LISSAGE)).thenReturn(bd("0.20"));
        when(parametres.entier(ClesParametres.APPRENTISSAGE_MIN_TRANSACTIONS_QUARTIER)).thenReturn(3);
        when(parametres.valeur(ClesParametres.APPRENTISSAGE_VARIATION_MAX_CYCLE)).thenReturn(bd("0.10"));
        when(parametres.valeur(ClesParametres.APPRENTISSAGE_COEFFICIENT_ZONE_MIN)).thenReturn(bd("0.50"));
        when(parametres.valeur(ClesParametres.APPRENTISSAGE_COEFFICIENT_ZONE_MAX)).thenReturn(bd("2.00"));
        when(transactionRepository.save(any(TransactionImmobiliere.class))).thenAnswer(i -> i.getArgument(0));

        service = new RecalibrageService(transactionRepository, quartierRepository, hedonique, parametres,
                parametrageService);
    }

    @Test
    void terrainNu_troisVentes_ajusteLeCoefficientEtIntegre() {
        List<TransactionImmobiliere> ventes = List.of(
                vente(terrain(), "60000000"), vente(terrain(), "60000000"), vente(terrain(), "60000000"));
        enAttente(ventes);
        hedoniqueRetourne("50000000", null);

        RecalibrageResponse reponse = service.recalibrer(false, acteur);

        // Ratio 60 / 50 = 1,2 ; coefficient 1,0000 × (1 + 0,20 × 0,2) = 1,0400
        assertThat(reponse.quartiers()).hasSize(1);
        assertThat(reponse.quartiers().get(0).ratioMedian()).isEqualByComparingTo("1.2");
        assertThat(reponse.quartiers().get(0).nouveauCoefficient()).isEqualByComparingTo("1.0400");
        assertThat(reponse.transactionsIntegrees()).isEqualTo(3);
        verify(parametrageService).modifier(eq("quartiers"), eq(10L), isNull(),
                eq(Map.of("coefficientZone", "1.0400")), anyString(), any(Acteur.class));
        assertThat(quartier.getNombreTransactionsCalibrage()).isEqualTo(3);
        assertThat(ventes).allMatch(TransactionImmobiliere::isIntegreApprentissage);
        assertThat(ventes).allMatch(t -> t.getDateIntegrationApprentissage() != null);
    }

    @Test
    void quartierSousLeMinimum_resteEnAttenteSansIntegration() {
        List<TransactionImmobiliere> ventes = List.of(
                vente(terrain(), "60000000"), vente(terrain(), "60000000"));
        enAttente(ventes);
        hedoniqueRetourne("50000000", null);

        RecalibrageResponse reponse = service.recalibrer(false, acteur);

        assertThat(reponse.quartiers()).isEmpty();
        assertThat(reponse.quartiersEnAttente()).hasSize(1);
        assertThat(reponse.quartiersEnAttente().get(0).transactionsDisponibles()).isEqualTo(2);
        assertThat(reponse.transactionsIntegrees()).isZero();
        verify(parametrageService, never()).modifier(anyString(), any(), any(), any(), anyString(), any());
        assertThat(ventes).noneMatch(TransactionImmobiliere::isIntegreApprentissage);
        assertThat(quartier.getNombreTransactionsCalibrage()).isZero();
    }

    @Test
    void bati_isoleLaPartTerrainEnRetranchantConstructionEtEquipements() {
        List<TransactionImmobiliere> ventes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            BienBati bati = bati();
            bati.ajouterEquipement(new Equipement(
                    new TypeEquipement("FORAGE", "Forage", bd("10000000")), null, null)); // 10 M (catalogue)
            ventes.add(vente(bati, "110000000"));
        }
        enAttente(ventes);
        hedoniqueRetourne("50000000", "40000000");

        RecalibrageResponse reponse = service.recalibrer(false, acteur);

        // Part terrain : 110 − 40 − 10 = 60 M ; ratio 60 / 50 = 1,2 → 1,0400
        assertThat(reponse.quartiers().get(0).ratioMedian()).isEqualByComparingTo("1.2");
        assertThat(reponse.quartiers().get(0).nouveauCoefficient()).isEqualByComparingTo("1.0400");
    }

    @Test
    void partTerrainNegative_estEcarteeEtResteEnAttente() {
        List<TransactionImmobiliere> ventes = List.of(vente(bati(), "30000000"));
        enAttente(ventes);
        hedoniqueRetourne("50000000", "35000000"); // construction > prix réel

        RecalibrageResponse reponse = service.recalibrer(false, acteur);

        assertThat(reponse.transactionsExaminees()).isEqualTo(1);
        assertThat(reponse.transactionsEcartees()).hasSize(1);
        assertThat(reponse.transactionsEcartees().get(0).raison()).contains("Part terrain");
        assertThat(reponse.quartiers()).isEmpty();
        assertThat(ventes.get(0).isIntegreApprentissage()).isFalse();
    }

    @Test
    void simulation_calculeSansRienEcrire() {
        List<TransactionImmobiliere> ventes = List.of(
                vente(terrain(), "60000000"), vente(terrain(), "60000000"), vente(terrain(), "60000000"));
        enAttente(ventes);
        hedoniqueRetourne("50000000", null);

        RecalibrageResponse reponse = service.recalibrer(true, acteur);

        assertThat(reponse.simulation()).isTrue();
        assertThat(reponse.quartiers().get(0).nouveauCoefficient()).isEqualByComparingTo("1.0400");
        verify(parametrageService, never()).modifier(anyString(), any(), any(), any(), anyString(), any());
        assertThat(ventes).noneMatch(TransactionImmobiliere::isIntegreApprentissage);
        assertThat(quartier.getNombreTransactionsCalibrage()).isZero();
        assertThat(quartier.getCoefficientZone()).isEqualByComparingTo("1");
    }

    @Test
    void parametreManquant_arreteToutSansRienEcrire() {
        when(parametres.valeur(ClesParametres.APPRENTISSAGE_LISSAGE)).thenThrow(
                new ParametreManquantException("ParametreGlobal", ClesParametres.APPRENTISSAGE_LISSAGE));

        assertThatThrownBy(() -> service.recalibrer(false, acteur))
                .isInstanceOf(ParametreManquantException.class)
                .hasMessageContaining(ClesParametres.APPRENTISSAGE_LISSAGE);
        verify(parametrageService, never()).modifier(anyString(), any(), any(), any(), anyString(), any());
    }

    @Test
    void bornesIncoherentes_leveIllegalState() {
        when(parametres.valeur(ClesParametres.APPRENTISSAGE_COEFFICIENT_ZONE_MIN)).thenReturn(bd("3.00")); // > max 2,00

        assertThatThrownBy(() -> service.recalibrer(true, acteur))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(ClesParametres.APPRENTISSAGE_COEFFICIENT_ZONE_MIN);
    }

    @Test
    void bati_locatif_troisLoyersConstates_ajusteLeTauxDuQuartier() {
        when(parametres.valeur(ClesParametres.CAPITALISATION_TAUX_CHARGES)).thenReturn(bd("0.15"));
        when(parametres.valeur(ClesParametres.CAPITALISATION_TAUX_DEFAUT)).thenReturn(bd("0.08"));
        when(parametres.valeur(ClesParametres.APPRENTISSAGE_TAUX_CAPITALISATION_MIN)).thenReturn(bd("0.03"));
        when(parametres.valeur(ClesParametres.APPRENTISSAGE_TAUX_CAPITALISATION_MAX)).thenReturn(bd("0.20"));
        List<TransactionImmobiliere> ventes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            BienBati bati = bati();
            bati.setLocatifOuCommercial(true);
            TransactionImmobiliere vente = vente(bati, "110000000");
            vente.setLoyerMensuelConstate(bd("500000"));
            ventes.add(vente);
        }
        enAttente(ventes);
        hedoniqueRetourne("50000000", "40000000");

        RecalibrageResponse reponse = service.recalibrer(false, acteur);

        // Taux observé : 500 000 × 12 × 0,85 ÷ 110 M = 0,046364 ; base 0,08 (défaut)
        // Ratio 0,57955 ; nouveau = 0,08 × (1 + 0,20 × (0,57955 − 1)) = 0,0733
        assertThat(reponse.rendements()).hasSize(1);
        assertThat(reponse.rendements().get(0).ancienTauxParDefaut()).isTrue();
        assertThat(reponse.rendements().get(0).nouveauTaux()).isEqualByComparingTo("0.0733");
        verify(parametrageService).modifier(eq("quartiers"), eq(10L), isNull(),
                eq(Map.of("tauxCapitalisation", "0.0733")), anyString(), any(Acteur.class));
    }

    @Test
    void terrainSansLoyer_neTouchePasLeTauxDuQuartier() {
        enAttente(List.of(vente(terrain(), "60000000"), vente(terrain(), "60000000"), vente(terrain(), "60000000")));
        hedoniqueRetourne("50000000", null);

        RecalibrageResponse reponse = service.recalibrer(false, acteur);

        assertThat(reponse.rendements()).isEmpty();
        verify(parametrageService, never()).modifier(anyString(), any(), any(),
                eq(Map.of("tauxCapitalisation", "0.0800")), anyString(), any());
    }
    // ---------- Fabriques ----------

    private void enAttente(List<TransactionImmobiliere> ventes) {
        when(transactionRepository.findByStatutAndIntegreApprentissageFalse(StatutTransaction.VALIDEE))
                .thenReturn(ventes);
    }

    /** Détail hédonique simulé : valeur du terrain, et valeur de la construction après vétusté pour un bâti. */
    private void hedoniqueRetourne(String valeurTerrain, String valeurConstruction) {
        Map<String, Object> detail = valeurConstruction == null
                ? Map.of("terrain", Map.of("valeurTerrain", bd(valeurTerrain)))
                : Map.of("terrain", Map.of("valeurTerrain", bd(valeurTerrain)),
                        "construction", Map.of("valeurApresVetuste", bd(valeurConstruction)));
        when(hedonique.estimer(any(Bien.class))).thenReturn(new ResultatMethodeCalcule(
                MethodeEstimation.HEDONIQUE, bd("1"), bd("2"), bd("3"), bd("0.5"), detail, List.of()));
    }

    private TerrainNu terrain() {
        TerrainNu terrain = new TerrainNu();
        ReflectionTestUtils.setField(terrain, "id", sequence++);
        terrain.setQuartier(quartier);
        return terrain;
    }

    private BienBati bati() {
        BienBati bati = new BienBati();
        ReflectionTestUtils.setField(bati, "id", sequence++);
        bati.setQuartier(quartier);
        return bati;
    }

    private TransactionImmobiliere vente(Bien bien, String prix) {
        TransactionImmobiliere transaction = new TransactionImmobiliere();
        ReflectionTestUtils.setField(transaction, "id", sequence++);
        transaction.setBien(bien);
        transaction.setPrixVenteEffectif(bd(prix));
        transaction.setDateTransaction(LocalDate.now());
        transaction.setStatut(StatutTransaction.VALIDEE);
        return transaction;
    }

    private static BigDecimal bd(String valeur) {
        return new BigDecimal(valeur);
    }
}