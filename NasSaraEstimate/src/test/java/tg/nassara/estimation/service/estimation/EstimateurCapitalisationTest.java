package tg.nassara.estimation.service.estimation;

import org.junit.jupiter.api.Test;
import tg.nassara.estimation.entity.AnnonceMarche;
import tg.nassara.estimation.entity.BienBati;
import tg.nassara.estimation.entity.TerrainNu;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.enums.MethodeEstimation;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.OrigineAnnonce;
import tg.nassara.estimation.enums.PeriodiciteLoyer;
import tg.nassara.estimation.enums.TypeAnnonce;
import tg.nassara.estimation.exception.DonneesInsuffisantesException;
import tg.nassara.estimation.exception.ParametreManquantException;
import tg.nassara.estimation.repository.AnnonceMarcheRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Tests unitaires de la capitalisation : calculs chiffrés à la main, sans base de données. */
class EstimateurCapitalisationTest {

    private static final List<String> CLES_ENTIERES = List.of(
            ClesParametres.MARCHE_FENETRE_JOURS,
            ClesParametres.MARCHE_MAX_COMPARABLES,
            ClesParametres.CAPITALISATION_MIN_COMPARABLES);

    private static final List<String> TOUTES_LES_CLES = List.of(
            ClesParametres.MARCHE_FENETRE_JOURS,
            ClesParametres.MARCHE_MAX_COMPARABLES,
            ClesParametres.CAPITALISATION_TAUX_DEFAUT,
            ClesParametres.CAPITALISATION_TAUX_CHARGES,
            ClesParametres.CAPITALISATION_MIN_COMPARABLES,
            ClesParametres.CAPITALISATION_NUITEES_PAR_MOIS,
            ClesParametres.CAPITALISATION_FACTEUR_MEUBLE,
            ClesParametres.CAPITALISATION_MARGE_FOURCHETTE,
            ClesParametres.CAPITALISATION_ECART_ALERTE_LOYER_BIEN,
            ClesParametres.CAPITALISATION_CONFIANCE_MIN,
            ClesParametres.CAPITALISATION_CONFIANCE_MAX,
            ClesParametres.CAPITALISATION_PENALITE_DISPERSION,
            ClesParametres.CAPITALISATION_CONFIANCE_FACTEUR_TAUX_DEFAUT,
            ClesParametres.CAPITALISATION_CONFIANCE_FACTEUR_LOYER_BIEN);

    private static final Map<String, String> PARAMETRES_STANDARD = Map.ofEntries(
            Map.entry(ClesParametres.MARCHE_FENETRE_JOURS, "180"),
            Map.entry(ClesParametres.MARCHE_MAX_COMPARABLES, "10"),
            Map.entry(ClesParametres.CAPITALISATION_TAUX_DEFAUT, "0.08"),
            Map.entry(ClesParametres.CAPITALISATION_TAUX_CHARGES, "0.15"),
            Map.entry(ClesParametres.CAPITALISATION_MIN_COMPARABLES, "2"),
            Map.entry(ClesParametres.CAPITALISATION_NUITEES_PAR_MOIS, "0"),
            Map.entry(ClesParametres.CAPITALISATION_FACTEUR_MEUBLE, "0.75"),
            Map.entry(ClesParametres.CAPITALISATION_MARGE_FOURCHETTE, "0.15"),
            Map.entry(ClesParametres.CAPITALISATION_ECART_ALERTE_LOYER_BIEN, "0.25"),
            Map.entry(ClesParametres.CAPITALISATION_CONFIANCE_MIN, "0.20"),
            Map.entry(ClesParametres.CAPITALISATION_CONFIANCE_MAX, "0.80"),
            Map.entry(ClesParametres.CAPITALISATION_PENALITE_DISPERSION, "1.0"),
            Map.entry(ClesParametres.CAPITALISATION_CONFIANCE_FACTEUR_TAUX_DEFAUT, "0.80"),
            Map.entry(ClesParametres.CAPITALISATION_CONFIANCE_FACTEUR_LOYER_BIEN, "0.60"));

    @Test
    void marche_tauxParDefaut_calculeLaValeurEtLaFourchette() {
        BienBati bien = bati(quartier(null), true, null);
        List<AnnonceMarche> annonces = List.of(
                loyer("400000", PeriodiciteLoyer.MENSUEL, null, 1),
                loyer("500000", PeriodiciteLoyer.MENSUEL, null, 2),
                loyer("600000", PeriodiciteLoyer.MENSUEL, null, 3));

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD, annonces).estimer(bien);

        // Loyers : médiane 500 000, Q1 450 000, Q3 550 000 ; × 12 × 0,85 ÷ 0,08
        assertThat(resultat.methode()).isEqualTo(MethodeEstimation.CAPITALISATION);
        assertThat(resultat.prixCentral()).isEqualByComparingTo("63750000");
        assertThat(resultat.prixMin()).isEqualByComparingTo("57375000");
        assertThat(resultat.prixMax()).isEqualByComparingTo("70125000");
        // Confiance : (0,20 + 0,60 × 0,3 × (1 − 0,1633)) × 0,80 (taux par défaut) ≈ 0,2805
        assertThat(resultat.indiceConfiance().doubleValue()).isCloseTo(0.2805, within(0.0005));
        assertThat(resultat.avertissements()).anyMatch(a -> a.contains("Taux de capitalisation par défaut"));
        assertThat(resultat.detail().get("sourceLoyer")).isEqualTo("MARCHE");
        assertThat((List<?>) resultat.detail().get("comparables")).hasSize(3);
    }

    @Test
    void marche_tauxDuQuartier_neSignalePasLeTauxParDefaut() {
        BienBati bien = bati(quartier("0.10"), true, null);
        List<AnnonceMarche> annonces = List.of(
                loyer("400000", PeriodiciteLoyer.MENSUEL, null, 1),
                loyer("500000", PeriodiciteLoyer.MENSUEL, null, 2),
                loyer("600000", PeriodiciteLoyer.MENSUEL, null, 3));

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD, annonces).estimer(bien);

        // 500 000 × 12 × 0,85 ÷ 0,10 = 51 000 000 ; confiance sans réduction : 0,3506
        assertThat(resultat.prixCentral()).isEqualByComparingTo("51000000");
        assertThat(resultat.indiceConfiance().doubleValue()).isCloseTo(0.3506, within(0.0005));
        assertThat(resultat.avertissements()).noneMatch(a -> a.contains("par défaut"));
        assertThat(resultat.detail().get("tauxSource")).isEqualTo("QUARTIER");
    }

    @Test
    void marche_loyerMeuble_estRamenéAuNonMeuble() {
        BienBati bien = bati(quartier(null), true, null);
        // 800 000 meublé × 0,75 = 600 000 ; 400 000 non précisé : médiane 500 000.
        List<AnnonceMarche> annonces = List.of(
                loyer("800000", PeriodiciteLoyer.MENSUEL, Boolean.TRUE, 1),
                loyer("400000", PeriodiciteLoyer.MENSUEL, null, 2));

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD, annonces).estimer(bien);

        assertThat(resultat.prixCentral()).isEqualByComparingTo("63750000");
        assertThat(resultat.avertissements()).anyMatch(a -> a.contains("meublé"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> comparables = (List<Map<String, Object>>) resultat.detail().get("comparables");
        assertThat((BigDecimal) comparables.get(0).get("loyerMensuelNormalise")).isEqualByComparingTo("600000");
    }

    @Test
    void marche_nuiteesIgnoreesQuandLeParametreVautZero() {
        BienBati bien = bati(quartier(null), true, null);
        List<AnnonceMarche> annonces = List.of(
                loyer("400000", PeriodiciteLoyer.MENSUEL, null, 1),
                loyer("600000", PeriodiciteLoyer.MENSUEL, null, 2),
                loyer("50000", PeriodiciteLoyer.NUITEE, null, 3));

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD, annonces).estimer(bien);

        // Seuls 400 000 et 600 000 sont retenus : médiane 500 000.
        assertThat(resultat.prixCentral()).isEqualByComparingTo("63750000");
        assertThat(resultat.detail().get("nombreLoyersRetenus")).isEqualTo(2);
        assertThat(resultat.avertissements()).anyMatch(a -> a.contains("nuitée") && a.contains("ignorée"));
    }

    @Test
    void marche_nuiteesConverties_ajouteLeLoyerEquivalent() {
        BienBati bien = bati(quartier(null), true, null);
        Map<String, String> parametres = new HashMap<>(PARAMETRES_STANDARD);
        parametres.put(ClesParametres.CAPITALISATION_NUITEES_PAR_MOIS, "10");
        List<AnnonceMarche> annonces = List.of(
                loyer("400000", PeriodiciteLoyer.MENSUEL, null, 1),
                loyer("600000", PeriodiciteLoyer.MENSUEL, null, 2),
                loyer("50000", PeriodiciteLoyer.NUITEE, null, 3)); // 50 000 × 10 = 500 000

        ResultatMethodeCalcule resultat = estimateur(parametres, annonces).estimer(bien);

        assertThat(resultat.detail().get("nombreLoyersRetenus")).isEqualTo(3);
        assertThat(resultat.prixCentral()).isEqualByComparingTo("63750000");
        assertThat(resultat.avertissements()).anyMatch(a -> a.contains("converti(s) depuis un tarif à la nuitée"));
    }

    @Test
    void loyersInsuffisants_replieSurLeLoyerActuelDuBien() {
        BienBati bien = bati(quartier(null), true, "450000");
        List<AnnonceMarche> annonces = List.of(loyer("400000", PeriodiciteLoyer.MENSUEL, null, 1));

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD, annonces).estimer(bien);

        // 450 000 × 12 × 0,85 ÷ 0,08 = 57 375 000 ; marge 15 % : × 0,85 et × 1,15
        assertThat(resultat.prixCentral()).isEqualByComparingTo("57375000");
        assertThat(resultat.prixMin()).isEqualByComparingTo("48768750");
        assertThat(resultat.prixMax()).isEqualByComparingTo("65981250");
        // Confiance : 0,20 × 0,60 (loyer du bien) × 0,80 (taux par défaut) = 0,096
        assertThat(resultat.indiceConfiance()).isEqualByComparingTo("0.096");
        assertThat(resultat.detail().get("sourceLoyer")).isEqualTo("LOYER_BIEN");
        assertThat(resultat.avertissements()).anyMatch(a -> a.contains("loyer actuel du bien"));
    }

    @Test
    void marche_loyerDuBienTresEloigne_signaleUnEcartSansChangerLaValeur() {
        BienBati bien = bati(quartier(null), true, "800000");
        List<AnnonceMarche> annonces = List.of(
                loyer("400000", PeriodiciteLoyer.MENSUEL, null, 1),
                loyer("500000", PeriodiciteLoyer.MENSUEL, null, 2),
                loyer("600000", PeriodiciteLoyer.MENSUEL, null, 3));

        ResultatMethodeCalcule resultat = estimateur(PARAMETRES_STANDARD, annonces).estimer(bien);

        // Le marché reste prioritaire ; écart (800 − 500) / 500 = 60 % > 25 %.
        assertThat(resultat.prixCentral()).isEqualByComparingTo("63750000");
        assertThat(resultat.avertissements()).anyMatch(a -> a.contains("s'écarte"));
    }

    @Test
    void loyersInsuffisantsSansLoyerDuBien_leveDonneesInsuffisantes() {
        BienBati bien = bati(quartier(null), true, null);
        List<AnnonceMarche> annonces = List.of(loyer("400000", PeriodiciteLoyer.MENSUEL, null, 1));

        assertThatThrownBy(() -> estimateur(PARAMETRES_STANDARD, annonces).estimer(bien))
                .isInstanceOf(DonneesInsuffisantesException.class)
                .hasMessageContaining("loyers comparables")
                .hasMessageContaining("minimum requis : 2");
    }

    @Test
    void estApplicable_seulementPourLeBatiLocatifOuCommercial() {
        EstimateurCapitalisation estimateur = estimateur(PARAMETRES_STANDARD, List.of());
        Quartier quartier = quartier(null);

        assertThat(estimateur.estApplicable(bati(quartier, true, null))).isTrue();
        assertThat(estimateur.estApplicable(bati(quartier, false, null))).isFalse();
        assertThat(estimateur.estApplicable(new TerrainNu())).isFalse();
        assertThat(estimateur.estApplicable(null)).isFalse();
    }

    @Test
    void parametreManquant_leveParametreManquantException() {
        BienBati bien = bati(quartier(null), true, null);
        Map<String, String> sansCharges = new HashMap<>(PARAMETRES_STANDARD);
        sansCharges.remove(ClesParametres.CAPITALISATION_TAUX_CHARGES);

        assertThatThrownBy(() -> estimateur(sansCharges, List.of()).estimer(bien))
                .isInstanceOf(ParametreManquantException.class)
                .hasMessageContaining(ClesParametres.CAPITALISATION_TAUX_CHARGES);
    }

    @Test
    void tauxDeQuartierIncoherent_leveIllegalState() {
        BienBati bien = bati(quartier("8"), true, null); // 8 au lieu de 0,08

        assertThatThrownBy(() -> estimateur(PARAMETRES_STANDARD, List.of()).estimer(bien))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("taux de capitalisation");
    }

    // ---------- Fabriques ----------

    private EstimateurCapitalisation estimateur(Map<String, String> valeurs, List<AnnonceMarche> annonces) {
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
        when(annonceRepository.findLoyersComparables(any(), any(), any(), any())).thenReturn(annonces);
        return new EstimateurCapitalisation(parametres, annonceRepository);
    }

    private static Quartier quartier(String tauxCapitalisation) {
        Quartier quartier = new Quartier("TEST", "Quartier test", new BigDecimal("100000"));
        if (tauxCapitalisation != null) {
            quartier.setTauxCapitalisation(new BigDecimal(tauxCapitalisation));
        }
        return quartier;
    }

    private static BienBati bati(Quartier quartier, boolean locatifOuCommercial, String loyerActuel) {
        BienBati bati = new BienBati();
        bati.setQuartier(quartier);
        bati.setLocatifOuCommercial(locatifOuCommercial);
        if (loyerActuel != null) {
            bati.setLoyerMensuelActuel(new BigDecimal(loyerActuel));
        }
        return bati;
    }

    private static AnnonceMarche loyer(String montant, PeriodiciteLoyer periodicite, Boolean meuble, int index) {
        AnnonceMarche annonce = new AnnonceMarche();
        annonce.setSource("TEST");
        annonce.setOrigine(OrigineAnnonce.SAISIE_AGENT);
        annonce.setUrlSource("https://exemple.test/loyer/" + index);
        annonce.setTitre("Location " + index);
        annonce.setTypeAnnonce(TypeAnnonce.LOCATION);
        annonce.setNature(NatureBien.BATI);
        annonce.setPeriodicite(periodicite);
        annonce.setMeuble(meuble);
        annonce.setMontant(new BigDecimal(montant));
        annonce.setDateCollecte(LocalDate.now());
        return annonce;
    }
}