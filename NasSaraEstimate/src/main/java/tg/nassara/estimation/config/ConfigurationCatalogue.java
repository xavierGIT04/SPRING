package tg.nassara.estimation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Configuration;

/**
 * Réglages TECHNIQUES de la lecture du catalogue (adresse, délais). Les paramètres de calcul,
 * eux, restent en base et modifiables par les agents.
 */
@Configuration
@EnableConfigurationProperties(ConfigurationCatalogue.Proprietes.class)
public class ConfigurationCatalogue {

    @ConfigurationProperties(prefix = "nassara.catalogue")
    public record Proprietes(
            @DefaultValue("https://nassarraimmobilier.com/notre-catalogue/") String url,
            @DefaultValue("10") int delaiSecondes,
            @DefaultValue("5") int dureeCacheMinutes) {
    }
}
