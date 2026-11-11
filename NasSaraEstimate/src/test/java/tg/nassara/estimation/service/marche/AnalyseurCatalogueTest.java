package tg.nassara.estimation.service.marche;

import org.junit.jupiter.api.Test;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.PeriodiciteLoyer;
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
    private static final String HTML_LOYERS = """
            <html><body><main>
            <div class="carte">
              <a href="/status/villa/">VILLA</a>
              <h2><a href="/Propriété/villa-meublee-a-louer-baguida/">Villa meublée à louer – Baguida</a></h2>
              <ul><li>65,000<span>F CFA</span><span>/La nuitée</span></li></ul>
              <span>LOUER</span>
            </div>
            <div class="carte">
              <a href="/status/villa/">VILLA</a>
              <h2><a href="/Propriété/villa-a-louer-agoe/">Villa à louer – Agoè</a></h2>
              <ul><li>450,000F CFA</li></ul>
              <span>LOUER</span>
            </div>
            <div class="carte">
              <a href="/status/appartement/">APPARTEMENT</a>
              <h2><a href="/Propriété/appartement-non-meuble-a-louer-tokoin/">Appartement non meublé à louer – Tokoin</a></h2>
              <ul><li>300,000F CFA/Mois</li></ul>
              <span>LOUER</span>
            </div>
            <div class="carte">
              <a href="/status/villa/">VILLA</a>
              <h2><a href="/Propriété/villa-a-louer-kodjoviakope/">Villa à louer – Kodjoviakopé</a></h2>
              <ul><li>150,000F CFA/Semaine</li></ul>
              <span>LOUER</span>
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
    
    @Test
    void analyser_litLaPeriodiciteEtLeMeubleDesLocations() {
        List<AnnonceCollectee> annonces = AnalyseurCatalogue.analyser(HTML_LOYERS, BASE);

        assertThat(annonces).hasSize(4);

        AnnonceCollectee nuitee = annonces.get(0);
        assertThat(nuitee.typeAnnonce()).isEqualTo(TypeAnnonce.LOCATION);
        assertThat(nuitee.montant()).isEqualByComparingTo("65000");
        assertThat(nuitee.periodicite()).isEqualTo(PeriodiciteLoyer.NUITEE);
        assertThat(nuitee.meuble()).isTrue();

        AnnonceCollectee sansSuffixe = annonces.get(1);
        assertThat(sansSuffixe.montant()).isEqualByComparingTo("450000");
        assertThat(sansSuffixe.periodicite()).isEqualTo(PeriodiciteLoyer.MENSUEL);
        assertThat(sansSuffixe.meuble()).isNull();

        AnnonceCollectee mensuel = annonces.get(2);
        assertThat(mensuel.periodicite()).isEqualTo(PeriodiciteLoyer.MENSUEL);
        assertThat(mensuel.meuble()).isFalse();

        AnnonceCollectee semaine = annonces.get(3);
        assertThat(semaine.periodicite()).isNull(); // suffixe inconnu : non retenue par le persisteur
    }

    @Test
    void determinerPeriodicite_appliqueLesReglesDeSuffixe() {
        assertThat(AnalyseurCatalogue.determinerPeriodicite("65,000F CFA/La nuitée", "")).isEqualTo(PeriodiciteLoyer.NUITEE);
        assertThat(AnalyseurCatalogue.determinerPeriodicite("300,000F CFA/Mois", "")).isEqualTo(PeriodiciteLoyer.MENSUEL);
        assertThat(AnalyseurCatalogue.determinerPeriodicite("600,000F CFA", "Villa à louer")).isEqualTo(PeriodiciteLoyer.MENSUEL);
        assertThat(AnalyseurCatalogue.determinerPeriodicite("65,000F CFA", "Villa à la nuitée")).isEqualTo(PeriodiciteLoyer.NUITEE);
        assertThat(AnalyseurCatalogue.determinerPeriodicite("150,000F CFA/Semaine", "")).isNull();
        assertThat(AnalyseurCatalogue.determinerPeriodicite("Prix sur demande", "")).isNull();
        assertThat(AnalyseurCatalogue.determinerPeriodicite(null, "")).isNull();
    }

    @Test
    void determinerMeuble_distingueMeubleNonMeubleEtInconnu() {
        assertThat(AnalyseurCatalogue.determinerMeuble("Villa meublée à louer")).isTrue();
        assertThat(AnalyseurCatalogue.determinerMeuble("Appartement non meublé")).isFalse();
        assertThat(AnalyseurCatalogue.determinerMeuble("Appartement non-meublé")).isFalse();
        assertThat(AnalyseurCatalogue.determinerMeuble("Villa à louer")).isNull();
        assertThat(AnalyseurCatalogue.determinerMeuble(null)).isNull();
    }
}
