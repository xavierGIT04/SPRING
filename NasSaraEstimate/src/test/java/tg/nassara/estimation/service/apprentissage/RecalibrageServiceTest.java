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
import tg.nassara.estimation.entity.reference.Standing;
import tg.nassara.estimation.entity.reference.TypeEquipement;
import tg.nassara.estimation.enums.MethodeEstimation;
import tg.nassara.estimation.enums.StatutTransaction;
import tg.nassara.estimation.exception.ParametreManquantException;
import tg.nassara.estimation.repository.QuartierRepository;
import tg.nassara.estimation.repository.StandingRepository;
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

/** Tests unitaires du recalibrage : tout est simulé (aucune base). Chiffres calculés à la main. */
class RecalibrageServiceTest {

    private final Acteur acteur = Acteur.agent("agent.test");

    private TransactionRepository transactionRepository;
    private QuartierRepository quartierRepository;
    private StandingRepository standingRepository;
    private EstimateurHedonique hedonique;
    private ParametresCalcul parametres;
    private ParametrageService parametrageService;
    private RecalibrageService service;
    private Quartier quartier;
    private Standing standing;
    private long sequence = 100;

    @BeforeEach
    void preparer() {
        transactionRepository = mock(TransactionRepository.class);
        quartierRepository = mock(QuartierRepository.class);
        standingRepository = mock(StandingRepository.class);
        hedonique = mock(EstimateurHedonique.class);
        parametres = mock(ParametresCalcul.class);
        parametrageService = mock(ParametrageService.class);

        quartier = new Quartier("TOKOIN", "Tokoin", bd("100000"));
        ReflectionTestUtils.setField(quartier, "id", 10L);
        when(quartierRepository.findById(10L)).thenReturn(Optional.of(quartier));

        standing = new Standing("MOYEN", "Moyen", bd("250000"), bd("0.02"), bd("0.70"));
        ReflectionTestUtils.setField(standing, "id", 5L);
        when(standingRepository.findById(5L)).thenReturn(Optional.of(standing));

        when(parametres.valeur(ClesParametres.APPRENTISSAGE_LISSAGE)).thenReturn(bd("0.20"));
        when(parametres.entier(ClesParametres.APPRENTISSAGE_MIN_TRANSACTIONS_QUARTIER)).thenReturn(3);
        when(parametres.valeur(ClesParametres.APPRENTISSAGE_VARIATION_MAX_CYCLE)).thenReturn(bd("0.10"));
        when(parametres.valeur(ClesParametres.APPRENTISSAGE_COEFFICIENT_ZONE_MIN)).thenReturn(bd("0.50"));
        when(parametres.valeur(ClesParametres.APPRENTISSAGE_COEFFICIENT_ZONE_MAX)).thenReturn(bd("2.00"));
        when(transactionRepository.save(any(TransactionImmobiliere.class))).thenAnswer(i -> i.getArgument(0));

        service = new RecalibrageService(transactionRepository, quartierRepository, standingRepository,
                hedonique, parametres, parametrageService);
    }

    // ======================================================================
    // Coefficient de zone
    // ======================================================================

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

    // ======================================================================
    // Taux de capitalisation
    // ======================================================================

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

    // ======================================================================
    // Coût de construction par standing (étape 9c-coût)
    // ======================================================================

    @Test
    void cout_troisVentesDeQuartierCalibre_ajusteLeCoutDuStanding() {
        quartier.setNombreTransactionsCalibrage(20);
        parametresCout();
        List<TransactionImmobiliere> ventes = ventesBaties(3, "95000000");
        coutEnAttente(ventes);
        hedoniqueBati();

        RecalibrageResponse reponse = service.recalibrer(false, acteur);

        // Part construction : 95 − 50 (terrain hédonique) − 10 (forage) = 35 M
        // Dénominateur : 150 m² × 1,10 × (1 − 0,20) = 132 ; coût observé 35 M ÷ 132 = 265 151,52 XOF/m²
        // Ratio 265 151,52 ÷ 250 000 = 1,060606 ; nouveau = 250 000 × (1 + 0,20 × 0,060606) = 253 030
        assertThat(reponse.standings()).hasSize(1);
        assertThat(reponse.standings().get(0).ratioMedian()).isEqualByComparingTo("1.060606");
        assertThat(reponse.standings().get(0).coutObserveMedian()).isEqualByComparingTo("265151.52");
        assertThat(reponse.standings().get(0).nouveauCout()).isEqualByComparingTo("253030");
        assertThat(reponse.standings().get(0).limite()).isNull();
        assertThat(reponse.transactionsCoutIntegrees()).isEqualTo(3);
        verify(parametrageService).modifier(eq("standings"), eq(5L), isNull(),
                eq(Map.of("coutConstructionM2", "253030")), anyString(), any(Acteur.class));
        assertThat(standing.getNombreTransactionsCalibrage()).isEqualTo(3);
        assertThat(ventes).allMatch(TransactionImmobiliere::isIntegreCout);
        assertThat(ventes).allMatch(t -> t.getDateIntegrationCout() != null);
    }

    @Test
    void cout_quartierPasEncoreCalibre_resteEnAttenteSansEtreEcarte() {
        quartier.setNombreTransactionsCalibrage(5); // sous la cible de 20
        parametresCout();
        List<TransactionImmobiliere> ventes = ventesBaties(3, "95000000");
        coutEnAttente(ventes);
        hedoniqueBati();

        RecalibrageResponse reponse = service.recalibrer(false, acteur);

        assertThat(reponse.transactionsCoutEnAttenteCalibrage()).isEqualTo(3);
        assertThat(reponse.standings()).isEmpty();
        assertThat(reponse.transactionsEcartees()).isEmpty();
        verify(parametrageService, never()).modifier(eq("standings"), any(), any(), any(), anyString(), any());
        assertThat(ventes).noneMatch(TransactionImmobiliere::isIntegreCout);
    }

    @Test
    void cout_standingSousLeMinimum_resteEnAttente() {
        quartier.setNombreTransactionsCalibrage(20);
        parametresCout();
        List<TransactionImmobiliere> ventes = ventesBaties(2, "95000000"); // minimum de 3
        coutEnAttente(ventes);
        hedoniqueBati();

        RecalibrageResponse reponse = service.recalibrer(false, acteur);

        assertThat(reponse.standings()).isEmpty();
        assertThat(reponse.standingsEnAttente()).hasSize(1);
        assertThat(reponse.standingsEnAttente().get(0).transactionsDisponibles()).isEqualTo(2);
        assertThat(reponse.transactionsCoutIntegrees()).isZero();
        assertThat(ventes).noneMatch(TransactionImmobiliere::isIntegreCout);
        assertThat(standing.getNombreTransactionsCalibrage()).isZero();
    }

    @Test
    void cout_partConstructionNegative_estEcarteeEtResteEnAttente() {
        quartier.setNombreTransactionsCalibrage(20);
        parametresCout();
        List<TransactionImmobiliere> ventes = ventesBaties(1, "55000000"); // 55 − 50 − 10 = −5 M
        coutEnAttente(ventes);
        hedoniqueBati();

        RecalibrageResponse reponse = service.recalibrer(false, acteur);

        assertThat(reponse.transactionsEcartees()).hasSize(1);
        assertThat(reponse.transactionsEcartees().get(0).raison()).contains("Part construction");
        assertThat(reponse.standings()).isEmpty();
        assertThat(ventes.get(0).isIntegreCout()).isFalse();
    }

    @Test
    void cout_simulation_calculeSansRienEcrire() {
        quartier.setNombreTransactionsCalibrage(20);
        parametresCout();
        List<TransactionImmobiliere> ventes = ventesBaties(3, "95000000");
        coutEnAttente(ventes);
        hedoniqueBati();

        RecalibrageResponse reponse = service.recalibrer(true, acteur);

        assertThat(reponse.simulation()).isTrue();
        assertThat(reponse.standings().get(0).nouveauCout()).isEqualByComparingTo("253030");
        verify(parametrageService, never()).modifier(anyString(), any(), any(), any(), anyString(), any());
        assertThat(ventes).noneMatch(TransactionImmobiliere::isIntegreCout);
        assertThat(standing.getNombreTransactionsCalibrage()).isZero();
        assertThat(standing.getCoutConstructionM2()).isEqualByComparingTo("250000");
    }

    @Test
    void cout_bornesIncoherentes_leveIllegalState() {
        quartier.setNombreTransactionsCalibrage(20);
        parametresCout();
        when(parametres.valeur(ClesParametres.APPRENTISSAGE_COUT_CONSTRUCTION_MIN)).thenReturn(bd("2000000")); // > max
        coutEnAttente(ventesBaties(3, "95000000"));
        hedoniqueBati();

        assertThatThrownBy(() -> service.recalibrer(true, acteur))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(ClesParametres.APPRENTISSAGE_COUT_CONSTRUCTION_MIN);
    }

    // ---------- Fabriques ----------

    private void enAttente(List<TransactionImmobiliere> ventes) {
        when(transactionRepository.findByStatutAndIntegreApprentissageFalse(StatutTransaction.VALIDEE))
                .thenReturn(ventes);
    }

    private void coutEnAttente(List<TransactionImmobiliere> ventes) {
        when(transactionRepository.findByStatutAndIntegreCoutFalse(StatutTransaction.VALIDEE))
                .thenReturn(ventes);
    }

    private void parametresCout() {
        when(parametres.valeur(ClesParametres.HEDONIQUE_TRANSACTIONS_CALIBRAGE_CIBLE)).thenReturn(bd("20"));
        when(parametres.entier(ClesParametres.APPRENTISSAGE_MIN_TRANSACTIONS_STANDING)).thenReturn(3);
        when(parametres.valeur(ClesParametres.APPRENTISSAGE_COUT_CONSTRUCTION_MIN)).thenReturn(bd("80000"));
        when(parametres.valeur(ClesParametres.APPRENTISSAGE_COUT_CONSTRUCTION_MAX)).thenReturn(bd("1500000"));
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

    /**
     * Détail hédonique d'un bâti de référence : terrain 50 M, 150 m² bâtis, coefficient du type de bien 1,10,
     * vétusté retenue 20 %, coût actuel du standing 250 000 XOF/m².
     */
    private void hedoniqueBati() {
        Map<String, Object> detail = Map.of(
                "terrain", Map.of("valeurTerrain", bd("50000000")),
                "construction", Map.of(
                        "valeurApresVetuste", bd("33000000"),
                        "surfaceBatieM2", bd("150"),
                        "coefficientTypeBien", bd("1.10"),
                        "vetusteRetenue", bd("0.20"),
                        "coutConstructionM2", bd("250000")));
        when(hedonique.estimer(any(Bien.class))).thenReturn(new ResultatMethodeCalcule(
                MethodeEstimation.HEDONIQUE, bd("1"), bd("2"), bd("3"), bd("0.5"), detail, List.of()));
    }

    /** Ventes de bâtis du même standing, chacune avec un forage de 10 M (montant du catalogue). */
    private List<TransactionImmobiliere> ventesBaties(int nombre, String prix) {
        List<TransactionImmobiliere> ventes = new ArrayList<>();
        for (int i = 0; i < nombre; i++) {
            BienBati bati = bati();
            bati.ajouterEquipement(new Equipement(
                    new TypeEquipement("FORAGE", "Forage", bd("10000000")), null, null));
            ventes.add(vente(bati, prix));
        }
        return ventes;
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
        bati.setStanding(standing);
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