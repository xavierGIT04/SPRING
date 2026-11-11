package tg.nassara.estimation.service.estimation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tg.nassara.estimation.enums.MethodeEstimation;
import tg.nassara.estimation.exception.ParametreManquantException;
import tg.nassara.estimation.service.estimation.FusionResultats.Contribution;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Tests unitaires de la fusion : calculs chiffrés à la main, sans base de données. */
class FusionResultatsTest {

    private ParametresCalcul parametres;
    private FusionResultats fusion;

    @BeforeEach
    void preparer() {
        parametres = mock(ParametresCalcul.class);
        when(parametres.valeur(ClesParametres.FUSION_ECART_ALERTE_METHODES)).thenReturn(bd("0.30"));
        when(parametres.valeur(ClesParametres.FUSION_CONFIANCE_FACTEUR_METHODE_UNIQUE)).thenReturn(bd("0.80"));
        fusion = new FusionResultats(parametres);
    }

    @Test
    void deuxMethodes_ponderentParLaConfianceEtSignalentUnGrosEcart() {
        // Poids effectifs : 1 × 0,60 = 0,60 et 1 × 0,20 = 0,20 → parts 0,75 et 0,25.
        Contribution hedonique = contribution(MethodeEstimation.HEDONIQUE, "1", "72000000", "80000000", "88000000", "0.60");
        Contribution marche = contribution(MethodeEstimation.COMPARAISON_MARCHE, "1", "36000000", "40000000", "44000000", "0.20");

        FusionResultats.Resultat resultat = fusion.fusionner(List.of(hedonique, marche));

        // Central : 0,75 × 80 M + 0,25 × 40 M = 70 M ; min : 54 M + 9 M = 63 M ; max : 66 M + 11 M = 77 M
        assertThat(resultat.prixCentral()).isEqualByComparingTo("70000000");
        assertThat(resultat.prixMin()).isEqualByComparingTo("63000000");
        assertThat(resultat.prixMax()).isEqualByComparingTo("77000000");
        // Confiance : 0,75 × 0,60 + 0,25 × 0,20 = 0,50
        assertThat(resultat.indiceConfiance()).isEqualByComparingTo("0.50");
        assertThat(resultat.parts().get(0).partFusion()).isEqualByComparingTo("0.75");
        assertThat(resultat.parts().get(1).partFusion()).isEqualByComparingTo("0.25");
        // Écart : (80 − 40) / 70 = 57,1 % > 30 %
        assertThat(resultat.avertissements()).anyMatch(a -> a.contains("Écart de 57.1 %"));
    }

    @Test
    void deuxMethodesProches_neSignalentPasDEcart() {
        Contribution a = contribution(MethodeEstimation.HEDONIQUE, "1", "72000000", "80000000", "88000000", "0.60");
        Contribution b = contribution(MethodeEstimation.COMPARAISON_MARCHE, "1", "70200000", "78000000", "85800000", "0.60");

        FusionResultats.Resultat resultat = fusion.fusionner(List.of(a, b));

        // Parts égales : central = (80 M + 78 M) / 2 = 79 M ; confiance 0,60
        assertThat(resultat.prixCentral()).isEqualByComparingTo("79000000");
        assertThat(resultat.indiceConfiance()).isEqualByComparingTo("0.60");
        assertThat(resultat.avertissements()).noneMatch(m -> m.contains("Écart"));
    }

    @Test
    void methodeUnique_reduitLaConfianceEtLeSignale() {
        Contribution seule = contribution(MethodeEstimation.HEDONIQUE, "1", "72000000", "80000000", "88000000", "0.60");

        FusionResultats.Resultat resultat = fusion.fusionner(List.of(seule));

        assertThat(resultat.prixCentral()).isEqualByComparingTo("80000000");
        assertThat(resultat.prixMin()).isEqualByComparingTo("72000000");
        // 0,60 × 0,80 = 0,48
        assertThat(resultat.indiceConfiance()).isEqualByComparingTo("0.48");
        assertThat(resultat.avertissements()).anyMatch(m -> m.contains("Une seule méthode"));
    }

    @Test
    void confiancesNulles_replieSurLesSeulesPonderations() {
        // Poids 1 et 3 → parts 0,25 et 0,75 : central = 0,25 × 80 M + 0,75 × 40 M = 50 M
        Contribution a = contribution(MethodeEstimation.HEDONIQUE, "1", "72000000", "80000000", "88000000", "0");
        Contribution b = contribution(MethodeEstimation.COMPARAISON_MARCHE, "3", "36000000", "40000000", "44000000", "0");

        FusionResultats.Resultat resultat = fusion.fusionner(List.of(a, b));

        assertThat(resultat.prixCentral()).isEqualByComparingTo("50000000");
        assertThat(resultat.indiceConfiance()).isEqualByComparingTo("0");
        assertThat(resultat.avertissements()).anyMatch(m -> m.contains("confiance nul"));
    }

    @Test
    void parametreManquant_leveParametreManquantException() {
        when(parametres.valeur(ClesParametres.FUSION_ECART_ALERTE_METHODES))
                .thenThrow(new ParametreManquantException("ParametreGlobal", ClesParametres.FUSION_ECART_ALERTE_METHODES));
        Contribution a = contribution(MethodeEstimation.HEDONIQUE, "1", "72000000", "80000000", "88000000", "0.60");
        Contribution b = contribution(MethodeEstimation.COMPARAISON_MARCHE, "1", "70200000", "78000000", "85800000", "0.60");

        assertThatThrownBy(() -> fusion.fusionner(List.of(a, b)))
                .isInstanceOf(ParametreManquantException.class)
                .hasMessageContaining(ClesParametres.FUSION_ECART_ALERTE_METHODES);
    }

    private static Contribution contribution(MethodeEstimation methode, String poids, String min, String central,
                                             String max, String confiance) {
        return new Contribution(methode, bd(poids), new ResultatMethodeCalcule(methode, bd(min), bd(central),
                bd(max), bd(confiance), Map.of(), List.of()));
    }

    private static BigDecimal bd(String valeur) {
        return new BigDecimal(valeur);
    }
}
