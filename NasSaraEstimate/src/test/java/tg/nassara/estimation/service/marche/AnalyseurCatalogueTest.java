package tg.nassara.estimation.service.marche;

import org.junit.jupiter.api.Test;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.TypeAnnonce;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Le HTML ci-dessous est un EXEMPLE construit d'après le catalogue : une fois le code compilé, enregistrer la vraie
 * page (clic droit > Enregistrer sous, ou « Afficher le code source ») dans src/test/resources/catalogue-exemple.html
 * et ajouter un test qui la charge, pour vérifier le découpage sur le HTML réel.
 */
class AnalyseurCatalogueTest {

    private static final String BASE = "https://nassarraimmobilier.com/notre-catalogue/";

    private static final String HTML = """
            <html><body><main>
            <div class="carte">
              <a href="/status/terrain-nu/">TERRAIN NU</a> <a href="/status/terrain/">TERRAIN</a>
              <a href="/Propriété/terrain-a-vendre-avepozo/"><img src="a.png"></a>
              <h2><a href="/Propriété/terrain-a-vendre-avepozo/">Terrain à vendre, Avépozo</a></h2>
              <ul><li>40,000,000<span>F CFA</span></li></ul>
              <span>ACHETER</span>
              <ul><li>600 m²</li></ul>
            </div>
            <div class="carte">
              <a href="/status/villa/">VILLA</a> <a href="/status/maison/">MAISON</a>
              <h2><a href="/Propriété/villa-a-louer-avenou/">Villa à louer à Lomé – Avenou</a></h2>
              <ul><li>600,000F CFA</li></ul>
              <span>LOUER</span>
              <ul><li>Lits: 5</li><li>Thermes: 4</li></ul>
            </div>
            <div class="carte">
              <a href="/status/villa/">VILLA</a>
              <h2><a href="/Propriété/villa-a-vendre-baguida/">Villa à vendre – Baguida</a></h2>
              <p>Prix sur demande</p>
              <span>ACHETER</span>
              <ul><li>Lits: 4</li><li>165 m²</li></ul>
            </div>
            </main></body></html>
            """;

    @Test
    void analyser_decoupeUneAnnonceParBloc() {
        List<AnnonceCollectee> annonces = AnalyseurCatalogue.analyser(HTML, BASE);

        assertThat(annonces).hasSize(3);

        AnnonceCollectee terrain = annonces.get(0);
        assertThat(terrain.urlSource()).contains("terrain-a-vendre-avepozo");
        assertThat(terrain.titre()).isEqualTo("Terrain à vendre, Avépozo");
        assertThat(terrain.typeAnnonce()).isEqualTo(TypeAnnonce.VENTE);
        assertThat(terrain.nature()).isEqualTo(NatureBien.TERRAIN);
        assertThat(terrain.montant()).isEqualByComparingTo("40000000");
        assertThat(terrain.surfaceM2()).isEqualByComparingTo("600");
        assertThat(terrain.quartierBrut()).isEqualTo("Avépozo");

        AnnonceCollectee location = annonces.get(1);
        assertThat(location.typeAnnonce()).isEqualTo(TypeAnnonce.LOCATION);
        assertThat(location.nature()).isEqualTo(NatureBien.BATI);
        assertThat(location.montant()).isEqualByComparingTo("600000");
        assertThat(location.surfaceM2()).isNull();

        AnnonceCollectee surDemande = annonces.get(2);
        assertThat(surDemande.typeAnnonce()).isEqualTo(TypeAnnonce.VENTE);
        assertThat(surDemande.montant()).isNull();
        assertThat(surDemande.surfaceM2()).isEqualByComparingTo("165");
    }

    @Test
    void extraireMontant_litLesFormatsDuCatalogue() {
        assertThat(AnalyseurCatalogue.extraireMontant("83,000,000F CFA")).isEqualByComparingTo("83000000");
        assertThat(AnalyseurCatalogue.extraireMontant("1,200,000F CFA")).isEqualByComparingTo("1200000");
        assertThat(AnalyseurCatalogue.extraireMontant("65,000F CFA/La nuitée")).isEqualByComparingTo("65000");
        assertThat(AnalyseurCatalogue.extraireMontant("Prix sur demande")).isNull();
    }

    @Test
    void extraireSurface_neLitQueLesSurfaces() {
        assertThat(AnalyseurCatalogue.extraireSurface("600 m²")).isEqualByComparingTo("600");
        assertThat(AnalyseurCatalogue.extraireSurface("1 200 m²")).isEqualByComparingTo("1200");
        assertThat(AnalyseurCatalogue.extraireSurface("3000 m²")).isEqualByComparingTo("3000");
        assertThat(AnalyseurCatalogue.extraireSurface("Lits: 5")).isNull();
    }

    @Test
    void determinerNature_appliqueLesPrioritesDesEtiquettes() {
        assertThat(AnalyseurCatalogue.determinerNature(Set.of("TERRAIN", "TERRAIN-A-BATIR", "TERRAIN-NU")))
                .isEqualTo(NatureBien.TERRAIN);
        assertThat(AnalyseurCatalogue.determinerNature(Set.of("TERRAIN", "TERRAIN-BATI", "TERRAIN-NU")))
                .isEqualTo(NatureBien.BATI);
        assertThat(AnalyseurCatalogue.determinerNature(Set.of("MAISON", "PROPRIETE", "VILLA")))
                .isEqualTo(NatureBien.BATI);
        assertThat(AnalyseurCatalogue.determinerNature(Set.of("VILLA", "VILLA-INACHEVEE"))).isNull();
        assertThat(AnalyseurCatalogue.determinerNature(Set.of("BUREAUX"))).isNull();
    }
}
