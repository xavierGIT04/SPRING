package tg.nassara.estimation.service.marche;

import org.springframework.stereotype.Component;
import tg.nassara.estimation.config.ConfigurationCatalogue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/** Lit le catalogue en ligne de l'agence (son propre site). */
@Component
public class SourceCatalogueNasSarra implements SourceAnnonces {

    public static final String NOM = "CATALOGUE_NAS_SARRA";

    private final ConfigurationCatalogue.Proprietes proprietes;
    private final HttpClient client;

    public SourceCatalogueNasSarra(ConfigurationCatalogue.Proprietes proprietes) {
        this.proprietes = proprietes;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(proprietes.delaiSecondes()))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Override
    public String nom() {
        return NOM;
    }

    @Override
    public List<AnnonceCollectee> collecter() {
        HttpRequest requete = HttpRequest.newBuilder(URI.create(proprietes.url()))
                .timeout(Duration.ofSeconds(proprietes.delaiSecondes()))
                .header("User-Agent", "NasSaraEstimation/1.0 (usage interne de l'agence)")
                .header("Accept", "text/html")
                .GET()
                .build();
        try {
            HttpResponse<String> reponse = client.send(requete,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (reponse.statusCode() != 200) {
                throw new IllegalStateException("Catalogue injoignable : HTTP " + reponse.statusCode() + ".");
            }
            return AnalyseurCatalogue.analyser(reponse.body(), proprietes.url());
        } catch (IOException e) {
            throw new IllegalStateException("Catalogue injoignable : " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Lecture du catalogue interrompue.", e);
        }
    }
}
