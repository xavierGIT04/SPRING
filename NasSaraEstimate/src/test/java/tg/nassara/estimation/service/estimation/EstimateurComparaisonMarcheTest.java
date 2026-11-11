package tg.nassara.estimation.service.estimation;

import org.junit.jupiter.api.Test;
import tg.nassara.estimation.entity.AnnonceMarche;
import tg.nassara.estimation.entity.TerrainNu;
import tg.nassara.estimation.entity.TransactionImmobiliere;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.enums.MethodeEstimation;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.OrigineAnnonce;
import tg.nassara.estimation.enums.TypeAnnonce;
import tg.nassara.estimation.exception.DonneesInsuffisantesException;
import tg.nassara.estimation.exception.ParametreManquantException;
import tg.nassara.estimation.repository.AnnonceMarcheRepository;
import tg.nassara.estimation.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Tests unitaires de la comparaison de marché : calculs chiffrés à la main, sans base de données. */
class EstimateurComparaisonMarcheTest {

    private static final List<String> CLES_ENTIERES = List.of(
            ClesParametres.MARCHE_FENETRE_JOURS,
            ClesParametres.MARCHE_MIN_COMPARABLES,
            ClesParametres.MARCHE_MAX_COMPARABLES,
            ClesParametres.MARCHE_MIN_TRANSACTIONS_CORRECTION);

    private static final List<String> TOUTES_LES_CLES = List.of(
            ClesParametres.MARCHE_FENETRE_JOURS,
            ClesParametres.MARCHE_MIN_COMPARABLES,
            ClesParametres.MARCHE_MAX_COMPARABLES,
            ClesParametres.MARCHE_TOLERANCE_SURFACE,
            ClesParametres.MARCHE_DECOTE_NEGOCIATION_DEFAUT,
            ClesParametres.MARCHE_MIN_TRANSACTIONS_CORRECTION,
            ClesParametres.MARCHE_CORRECTION_MIN,
            ClesParametres.MARCHE_CORRECTION_MAX,
            ClesParametres.MARCHE_CONFIANCE_MIN,
            ClesParametres.MARCHE_CONFIANCE_MAX,
            ClesParametres.MARCHE_PENALITE_DISPERSION,
            ClesParametres.MARCHE_CONFIANCE_FACTEUR_DECOTE_DEFAUT);

    private static final Map<String, String> PARAMETRES_STANDARD = Map.ofEntries(
            Map.entry(ClesParametres.MARCHE_FENETRE_JOURS, "180"),
            Map.entry(ClesParametres.MARCHE_MIN_COMPARABLES, "2"),
            Map.entry(ClesParametres.MARCHE_MAX_COMPARABLES, "10"),
            Map.entry(ClesParametres.MARCHE_TOLERANCE_SURFACE, "0.30"),
            Map.entry(ClesParametres.MARCHE_DECOTE_NEGOCIATION_DEFAUT, "0.90"),
            Map.entry(ClesParametres.MARCHE_MIN_TRANSACTIONS_CORRECTION, "5"),
            Map.entry(ClesParametres.MARCHE_CORRECTION_MIN, "0.70"),
            Map.entry(ClesParametres.MARCHE_CORRECTION_MAX, "1.10"),
            Map.entry(ClesParametres.MARCHE_CONFIANCE_MIN, "0.20"),
            Map.entry(ClesParametres.MARCHE_CONFIANCE_MAX, "0.80"),
            Map.entry(ClesParametres.MARCHE_PENALITE_DISPERSION, "1.0"),
            Map.entry(ClesParametres.MARCHE_CONFIANCE_FACTEUR_DECOTE_DEFAUT, "0.70"));

    @Test
    void sansTransactions_appliqueLaDecoteParDefautEtCalculeLaFourchette() {
        Quartier quartier = quartier();
        TerrainNu bien = terrain(quartier, "600");
        // Prix au m² : 100 000, 80 000, 60 000 → médiane 80 000, Q1 70 000, Q3 90 000.
        List<AnnonceMarche> annonces = List.of(
                annonce("60000000", "600", 1), annonce("48000000", "600", 2), annonce("33000000", "550", 3));

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD, annonces, List.of()).estimer(bien);

        // Décote par défaut 0,90 : central 80 000 × 0,90 × 600 = 43 200 000
        assertThat(resultat.methode()).isEqualTo(MethodeEstimation.COMPARAISON_MARCHE);
        assertThat(resultat.prixCentral()).isEqualByComparingTo("43200000");
        assertThat(resultat.prixMin()).isEqualByComparingTo("37800000");   // 70 000 × 0,90 × 600
        assertThat(resultat.prixMax()).isEqualByComparingTo("48600000");   // 90 000 × 0,90 × 600
        // Confiance : (0,20 + 0,60 × 0,3 × (1 − 0,2041)) × 0,70 ≈ 0,2403
        assertThat(resultat.indiceConfiance().doubleValue()).isCloseTo(0.2403, within(0.0005));
        assertThat(resultat.avertissements()).anyMatch(a -> a.contains("décote de négociation par défaut"));
        assertThat((List<?>) resultat.detail().get("comparables")).hasSize(3);
    }

    @Test
    void avecAssezDeTransactions_corrigeSurLesPrixReels() {
        Quartier quartier = quartier();
        TerrainNu bien = terrain(quartier, "600");
        List<AnnonceMarche> annonces = List.of(
                annonce("60000000", "600", 1), annonce("48000000", "600", 2), annonce("33000000", "550", 3));
        // 5 transactions validées à 64 000 XOF/m² (32 000 000 pour 500 m²) → 64 000 / 80 000 = 0,80.
        List<TransactionImmobiliere> transactions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            transactions.add(transaction(quartier, "500", "32000000"));
        }

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD, annonces, transactions).estimer(bien);

        // 80 000 × 0,80 × 600 = 38 400 000
        assertThat(resultat.prixCentral()).isEqualByComparingTo("38400000");
        assertThat(resultat.avertissements()).noneMatch(a -> a.contains("par défaut"));
        @SuppressWarnings("unchecked")
        Map<String, Object> correction = (Map<String, Object>) resultat.detail().get("correction");
        assertThat(correction.get("source")).isEqualTo("TRANSACTIONS_VALIDEES");
    }

    @Test
    void moinsQueLeMinimum_leveDonneesInsuffisantesAvecMessageProfessionnel() {
        TerrainNu bien = terrain(quartier(), "600");
        List<AnnonceMarche> annonces = List.of(annonce("60000000", "600", 1));

        assertThatThrownBy(() -> estimateur(PARAMETRES_STANDARD, annonces, List.of()).estimer(bien))
                .isInstanceOf(DonneesInsuffisantesException.class)
                .hasMessageContaining("pas été concluante")
                .hasMessageContaining("minimum requis : 2");
    }

    @Test
    void auDelaDuMaximum_neRetientQueLesPlusProchesEnSurface() {
        TerrainNu bien = terrain(quartier(), "600");
        List<AnnonceMarche> annonces = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            int surface = 600 + 10 * i; // 600 à 710 m², tous à 100 000 XOF/m²
            annonces.add(annonce(String.valueOf(surface * 100000L), String.valueOf(surface), i));
        }

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD, annonces, List.of()).estimer(bien);

        assertThat(resultat.detail().get("nombreComparablesRetenus")).isEqualTo(10);
        assertThat((List<?>) resultat.detail().get("comparables")).hasSize(10);
    }

    @Test
    void parametreManquant_leveParametreManquantException() {
        TerrainNu bien = terrain(quartier(), "600");
        Map<String, String> sansTolerance = new HashMap<>(PARAMETRES_STANDARD);
        sansTolerance.remove(ClesParametres.MARCHE_TOLERANCE_SURFACE);

        assertThatThrownBy(() -> estimateur(sansTolerance, List.of(), List.of()).estimer(bien))
                .isInstanceOf(ParametreManquantException.class)
                .hasMessageContaining(ClesParametres.MARCHE_TOLERANCE_SURFACE);
    }

    // ---------- Fabriques ----------

    private EstimateurComparaisonMarche estimateur(Map<String, String> valeurs, List<AnnonceMarche> annonces,
                                                   List<TransactionImmobiliere> transactions) {
        ParametresCalcul parametres = mock(ParametresCalcul.class);
        for (String cle : TOUTES_LES_CLES) {
            String valeur = valeurs.get(cle);
            if (valeur == null) {
                when(parametres.valeur(cle)).thenThrow(new ParametreManquantException("ParametreGlobal", cle));
                if (CLES_ENTIERES.contains(cle)) {
                    when(parametres.entier(cle)).thenThrow(new ParametreManquantException("ParametreGlobal", cle));
                }
            } else {
                when(parametres.valeur(cle)).thenReturn(new BigDecimal(valeur));
                if (CLES_ENTIERES.contains(cle)) {
                    when(parametres.entier(cle)).thenReturn(Integer.parseInt(valeur));
                }
            }
        }
        AnnonceMarcheRepository annonceRepository = mock(AnnonceMarcheRepository.class);
        when(annonceRepository.findComparables(any(), any(), any(), any(), any(), any())).thenReturn(annonces);
        TransactionRepository transactionRepository = mock(TransactionRepository.class);
        when(transactionRepository.findHistoriqueParQuartier(any(), any())).thenReturn(transactions);
        return new EstimateurComparaisonMarche(parametres, annonceRepository, transactionRepository);
    }

    private static Quartier quartier() {
        return new Quartier("TEST", "Quartier test", new BigDecimal("100000"));
    }

    private static TerrainNu terrain(Quartier quartier, String surface) {
        TerrainNu terrain = new TerrainNu();
        terrain.setQuartier(quartier);
        terrain.setSurfaceTerrainM2(new BigDecimal(surface));
        return terrain;
    }

    private static AnnonceMarche annonce(String montant, String surface, int index) {
        AnnonceMarche annonce = new AnnonceMarche();
        annonce.setSource("TEST");
        annonce.setOrigine(OrigineAnnonce.SAISIE_AGENT);
        annonce.setUrlSource("https://exemple.test/" + index);
        annonce.setTitre("Annonce " + index);
        annonce.setTypeAnnonce(TypeAnnonce.VENTE);
        annonce.setNature(NatureBien.TERRAIN);
        annonce.setMontant(new BigDecimal(montant));
        annonce.setSurfaceTerrainM2(new BigDecimal(surface));
        annonce.setDateCollecte(LocalDate.now());
        return annonce;
    }

    private static TransactionImmobiliere transaction(Quartier quartier, String surface, String prixVente) {
        TransactionImmobiliere transaction = new TransactionImmobiliere();
        transaction.setBien(terrain(quartier, surface));
        transaction.setPrixVenteEffectif(new BigDecimal(prixVente));
        return transaction;
    }
}
