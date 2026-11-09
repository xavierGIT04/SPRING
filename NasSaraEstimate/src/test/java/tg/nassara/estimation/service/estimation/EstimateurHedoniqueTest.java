package tg.nassara.estimation.service.estimation;

import org.junit.jupiter.api.Test;
import tg.nassara.estimation.entity.BienBati;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.entity.Equipement;
import tg.nassara.estimation.entity.TerrainNu;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.entity.reference.Standing;
import tg.nassara.estimation.entity.reference.StatutJuridique;
import tg.nassara.estimation.entity.reference.Topographie;
import tg.nassara.estimation.entity.reference.TypeBien;
import tg.nassara.estimation.entity.reference.TypeEquipement;
import tg.nassara.estimation.entity.reference.TypeVoie;
import tg.nassara.estimation.entity.reference.Viabilisation;
import tg.nassara.estimation.enums.MethodeEstimation;
import tg.nassara.estimation.exception.DonneesInsuffisantesException;
import tg.nassara.estimation.exception.ParametreManquantException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires de la méthode hédonique : calculs chiffrés à la main, plafond de cumul, vétusté,
 * équipements, confiance et cas d'erreur. Aucune base de données.
 */
class EstimateurHedoniqueTest {

    private static final List<String> TOUTES_LES_CLES = List.of(
            ClesParametres.HEDONIQUE_MARGE_FOURCHETTE,
            ClesParametres.HEDONIQUE_CUMUL_COEFFICIENTS_MIN,
            ClesParametres.HEDONIQUE_CUMUL_COEFFICIENTS_MAX,
            ClesParametres.HEDONIQUE_CONFIANCE_MAX,
            ClesParametres.HEDONIQUE_CONFIANCE_MIN,
            ClesParametres.HEDONIQUE_TRANSACTIONS_CALIBRAGE_CIBLE,
            ClesParametres.HEDONIQUE_CONFIANCE_FACTEUR_PROVISOIRE);

    /** Jeu de paramètres de référence du test (identique aux valeurs de test proposées pour la mise en service). */
    private static final Map<String, String> PARAMETRES_STANDARD = Map.of(
            ClesParametres.HEDONIQUE_MARGE_FOURCHETTE, "0.10",
            ClesParametres.HEDONIQUE_CUMUL_COEFFICIENTS_MIN, "0.40",
            ClesParametres.HEDONIQUE_CUMUL_COEFFICIENTS_MAX, "1.80",
            ClesParametres.HEDONIQUE_CONFIANCE_MAX, "0.85",
            ClesParametres.HEDONIQUE_CONFIANCE_MIN, "0.30",
            ClesParametres.HEDONIQUE_TRANSACTIONS_CALIBRAGE_CIBLE, "20",
            ClesParametres.HEDONIQUE_CONFIANCE_FACTEUR_PROVISOIRE, "0.60");

    private static final ZoneId FUSEAU = ZoneId.of("Africa/Lome");

    // ======================================================================
    // Terrain nu
    // ======================================================================

    @Test
    void terrainNu_calculeLeProduitDesCoefficientsEtLaFourchette() {
        // Quartier : 100 000 XOF/m², coefficient de zone 1,10, 10 transactions de calibrage sur 20.
        Quartier quartier = quartier("100000", "1.10", 10, false);
        // 500 m², voie 1,10, topographie 1,00, statut juridique 1,20.
        TerrainNu terrain = terrain(quartier, "500", "1.10", "1.00", "1.20");
        terrain.getViabilisations().add(new Viabilisation("CEET", "CEET", bd("1.05")));
        terrain.getViabilisations().add(new Viabilisation("TDE", "TdE", bd("1.05")));

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD).estimer(terrain);

        // Produit des coefficients : 1,10 × 1,00 × 1,20 × 1,05 × 1,05 = 1,4553
        // Valeur : 500 × 100 000 × 1,10 × 1,4553 = 80 041 500
        assertThat(resultat.methode()).isEqualTo(MethodeEstimation.HEDONIQUE);
        assertThat(resultat.prixCentral()).isEqualByComparingTo("80041500");
        assertThat(resultat.prixMin()).isEqualByComparingTo("72037350");   // × 0,90
        assertThat(resultat.prixMax()).isEqualByComparingTo("88045650");   // × 1,10
        // Confiance : 0,30 + (0,85 − 0,30) × (10 / 20) = 0,575
        assertThat(resultat.indiceConfiance()).isEqualByComparingTo("0.575");
        assertThat(resultat.avertissements()).isEmpty();
        assertThat(resultat.detail()).containsKeys("terrain", "valeurTotale");
    }

    @Test
    void terrainNu_plafonneLeCumulDesCoefficients() {
        Quartier quartier = quartier("100000", "1.00", 10, false);
        // 1,5 × 1,3 × 1,5 = 2,925 → ramené au plafond de 1,80.
        TerrainNu terrain = terrain(quartier, "100", "1.5", "1.3", "1.5");

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD).estimer(terrain);

        // 100 × 100 000 × 1,00 × 1,80 = 18 000 000
        assertThat(resultat.prixCentral()).isEqualByComparingTo("18000000");
        assertThat(resultat.avertissements()).anyMatch(a -> a.contains("borné"));
    }

    @Test
    void terrainNu_releveLeCumulAuPlancher() {
        Quartier quartier = quartier("100000", "1.00", 10, false);
        // 0,5 × 0,5 × 0,5 = 0,125 → relevé au plancher de 0,40.
        TerrainNu terrain = terrain(quartier, "100", "0.5", "0.5", "0.5");

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD).estimer(terrain);

        // 100 × 100 000 × 1,00 × 0,40 = 4 000 000
        assertThat(resultat.prixCentral()).isEqualByComparingTo("4000000");
        assertThat(resultat.avertissements()).anyMatch(a -> a.contains("borné"));
    }

    @Test
    void terrainNu_ignoreLesEquipementsAvecAvertissement() {
        Quartier quartier = quartier("100000", "1.00", 10, false);
        TerrainNu terrain = terrain(quartier, "100", "1", "1", "1");
        terrain.ajouterEquipement(new Equipement(
                new TypeEquipement("CLOTURE", "Clôture", bd("1500000")), null, null));

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD).estimer(terrain);

        assertThat(resultat.prixCentral()).isEqualByComparingTo("10000000"); // 100 × 100 000, équipement ignoré
        assertThat(resultat.avertissements()).anyMatch(a -> a.contains("équipements"));
    }

    // ======================================================================
    // Bien bâti
    // ======================================================================

    @Test
    void bati_additionneTerrainConstructionVetusteEtEquipements() {
        Quartier quartier = quartier("80000", "1.00", 10, false);
        BienBati bati = bati(quartier, "300", anneeCourante() - 10);
        // Équipements : forage à montant propre, solaire au montant du catalogue.
        bati.ajouterEquipement(new Equipement(
                new TypeEquipement("FORAGE", "Forage", bd("2500000")), bd("3000000"), "Forage profond"));
        bati.ajouterEquipement(new Equipement(
                new TypeEquipement("SOLAIRE", "Solaire", bd("2000000")), null, null));

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD).estimer(bati);

        // Terrain       : 300 × 80 000 × 1,00 × 1,00                  = 24 000 000
        // Neuf          : 150 × 250 000 × 1,10 (type de bien)          = 41 250 000
        // Vétusté       : 2 % × 10 ans = 20 % → 41 250 000 × 0,80      = 33 000 000
        // Équipements   : 3 000 000 (propre) + 2 000 000 (catalogue)   =  5 000 000
        // Total                                                        = 62 000 000
        assertThat(resultat.prixCentral()).isEqualByComparingTo("62000000");
        assertThat(resultat.prixMin()).isEqualByComparingTo("55800000");
        assertThat(resultat.prixMax()).isEqualByComparingTo("68200000");
        assertThat(resultat.avertissements()).isEmpty();
        assertThat(resultat.detail()).containsKeys("terrain", "construction", "equipements", "totalEquipements");
    }

    @Test
    void bati_plafonneLaVetuste() {
        Quartier quartier = quartier("80000", "1.00", 10, false);
        BienBati bati = bati(quartier, "300", anneeCourante() - 50);

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD).estimer(bati);

        // Vétusté brute 2 % × 50 = 100 % → plafonnée à 70 % : 41 250 000 × 0,30 = 12 375 000
        // Total : 24 000 000 + 12 375 000 = 36 375 000
        assertThat(resultat.prixCentral()).isEqualByComparingTo("36375000");
        assertThat(resultat.avertissements()).anyMatch(a -> a.contains("plafonnée"));
    }

    @Test
    void bati_standingAbsent_leveDonneesInsuffisantes() {
        BienBati bati = bati(quartier("80000", "1.00", 10, false), "300", anneeCourante() - 10);
        bati.setStanding(null);

        assertThatThrownBy(() -> estimateur(PARAMETRES_STANDARD).estimer(bati))
                .isInstanceOf(DonneesInsuffisantesException.class)
                .hasMessageContaining("standing");
    }

    @Test
    void bati_anneeConstructionAbsente_leveDonneesInsuffisantes() {
        BienBati bati = bati(quartier("80000", "1.00", 10, false), "300", anneeCourante() - 10);
        bati.setAnneeConstruction(null);

        assertThatThrownBy(() -> estimateur(PARAMETRES_STANDARD).estimer(bati))
                .isInstanceOf(DonneesInsuffisantesException.class)
                .hasMessageContaining("année de construction");
    }

    // ======================================================================
    // Confiance et avertissements du quartier
    // ======================================================================

    @Test
    void confiance_estReduiteSiPrixDeBaseProvisoire() {
        Quartier quartier = quartier("100000", "1.00", 10, true);
        TerrainNu terrain = terrain(quartier, "100", "1", "1", "1");

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD).estimer(terrain);

        // 0,575 × 0,60 = 0,345
        assertThat(resultat.indiceConfiance()).isEqualByComparingTo("0.345");
        assertThat(resultat.avertissements()).anyMatch(a -> a.contains("provisoire"));
    }

    @Test
    void confiance_estPlafonneeAuMaximumAuDelaDeLaCible() {
        Quartier quartier = quartier("100000", "1.00", 40, false); // 40 > cible de 20
        TerrainNu terrain = terrain(quartier, "100", "1", "1", "1");

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD).estimer(terrain);

        assertThat(resultat.indiceConfiance()).isEqualByComparingTo("0.85");
    }

    @Test
    void quartierJamaisCalibre_confianceMinimaleEtAvertissement() {
        Quartier quartier = quartier("100000", "1.00", 0, false);
        TerrainNu terrain = terrain(quartier, "100", "1", "1", "1");

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD).estimer(terrain);

        assertThat(resultat.indiceConfiance()).isEqualByComparingTo("0.30");
        assertThat(resultat.avertissements()).anyMatch(a -> a.contains("jamais été calibré"));
    }

    // ======================================================================
    // Erreurs
    // ======================================================================

    @Test
    void terrainSansSurface_leveDonneesInsuffisantes() {
        TerrainNu terrain = terrain(quartier("100000", "1.00", 10, false), null, "1", "1", "1");

        assertThatThrownBy(() -> estimateur(PARAMETRES_STANDARD).estimer(terrain))
                .isInstanceOf(DonneesInsuffisantesException.class)
                .hasMessageContaining("surface du terrain");
    }

    @Test
    void parametreManquant_leveParametreManquantException() {
        TerrainNu terrain = terrain(quartier("100000", "1.00", 10, false), "100", "1", "1", "1");
        Map<String, String> sansMarge = new HashMap<>(PARAMETRES_STANDARD);
        sansMarge.remove(ClesParametres.HEDONIQUE_MARGE_FOURCHETTE);

        assertThatThrownBy(() -> estimateur(sansMarge).estimer(terrain))
                .isInstanceOf(ParametreManquantException.class)
                .hasMessageContaining(ClesParametres.HEDONIQUE_MARGE_FOURCHETTE);
    }

    @Test
    void bornesIncoherentes_leveIllegalState() {
        TerrainNu terrain = terrain(quartier("100000", "1.00", 10, false), "100", "1", "1", "1");
        Map<String, String> incoherent = new HashMap<>(PARAMETRES_STANDARD);
        incoherent.put(ClesParametres.HEDONIQUE_CUMUL_COEFFICIENTS_MIN, "2.00"); // min > max (1,80)

        assertThatThrownBy(() -> estimateur(incoherent).estimer(terrain))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(ClesParametres.HEDONIQUE_CUMUL_COEFFICIENTS_MIN);
    }

    // ======================================================================
    // Fabriques de données de test
    // ======================================================================

    private EstimateurHedonique estimateur(Map<String, String> valeurs) {
        ParametresCalcul parametres = mock(ParametresCalcul.class);
        for (String cle : TOUTES_LES_CLES) {
            String valeur = valeurs.get(cle);
            if (valeur == null) {
                when(parametres.valeur(cle)).thenThrow(new ParametreManquantException("ParametreGlobal", cle));
            } else {
                when(parametres.valeur(cle)).thenReturn(new BigDecimal(valeur));
            }
        }
        return new EstimateurHedonique(parametres);
    }

    private static Quartier quartier(String prixBase, String coefficientZone, int calibrages, boolean provisoire) {
        Quartier quartier = new Quartier("TEST", "Quartier test", bd(prixBase));
        quartier.setCoefficientZone(bd(coefficientZone));
        quartier.setNombreTransactionsCalibrage(calibrages);
        quartier.setPrixBaseProvisoire(provisoire);
        return quartier;
    }

    private static void renseignerSite(Bien bien, Quartier quartier, String surface,
                                       String voie, String topographie, String statut) {
        bien.setQuartier(quartier);
        if (surface != null) {
            bien.setSurfaceTerrainM2(bd(surface));
        }
        bien.setTypeVoie(new TypeVoie("VOIE", "Voie", bd(voie)));
        bien.setTopographie(new Topographie("TOPO", "Topographie", bd(topographie)));
        bien.setStatutJuridique(new StatutJuridique("STATUT", "Statut", bd(statut)));
    }

    private static TerrainNu terrain(Quartier quartier, String surface,
                                     String voie, String topographie, String statut) {
        TerrainNu terrain = new TerrainNu();
        renseignerSite(terrain, quartier, surface, voie, topographie, statut);
        return terrain;
    }

    /** Bâti de référence : 150 m² bâtis, standing à 250 000 XOF/m² (2 %/an, plafond 70 %), type de bien ×1,10. */
    private static BienBati bati(Quartier quartier, String surfaceTerrain, int anneeConstruction) {
        BienBati bati = new BienBati();
        renseignerSite(bati, quartier, surfaceTerrain, "1", "1", "1");
        bati.setSurfaceBatieM2(bd("150"));
        bati.setStanding(new Standing("MOYEN", "Moyen", bd("250000"), bd("0.02"), bd("0.70")));
        bati.setTypeBien(new TypeBien("VILLA", "Villa", bd("1.10")));
        bati.setAnneeConstruction(anneeConstruction);
        return bati;
    }

    private static int anneeCourante() {
        return LocalDate.now(FUSEAU).getYear();
    }

    private static BigDecimal bd(String valeur) {
        return new BigDecimal(valeur);
    }
}