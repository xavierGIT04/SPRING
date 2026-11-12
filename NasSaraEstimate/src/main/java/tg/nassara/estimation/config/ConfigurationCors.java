package tg.nassara.estimation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS pour le front Angular. Inutile en développement (le proxy d'Angular évite le cross-origin) ;
 * nécessaire quand le front est servi depuis une autre origine. Réglage technique : nassara.cors.origines.
 */
@Configuration
public class ConfigurationCors implements WebMvcConfigurer {

    private final String[] origines;

    public ConfigurationCors(@Value("${nassara.cors.origines}") String[] origines) {
        this.origines = origines;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(origines)
                .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Accept", ConfigurationWeb.ENTETE_UTILISATEUR)
                .maxAge(3600);
    }
}
