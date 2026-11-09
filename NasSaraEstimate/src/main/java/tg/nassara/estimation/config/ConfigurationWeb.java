package tg.nassara.estimation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tg.nassara.estimation.exception.DonneesInvalidesException;
import tg.nassara.estimation.service.parametrage.Acteur;

import java.util.List;

@Configuration
public class ConfigurationWeb implements WebMvcConfigurer {

    public static final String ENTETE_UTILISATEUR = "X-Utilisateur";

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ActeurArgumentResolver());
    }

    /**
     * Fournit l'Acteur courant aux méthodes de contrôleur.
     * Aujourd'hui : simple en-tête, non authentifié. À remplacer par l'identité d'une vraie authentification.
     */
    private static final class ActeurArgumentResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return Acteur.class.equals(parameter.getParameterType());
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer conteneur,
                                      NativeWebRequest requete, WebDataBinderFactory fabrique) {
            String identifiant = requete.getHeader(ENTETE_UTILISATEUR);
            if (identifiant == null || identifiant.isBlank()) {
                throw new DonneesInvalidesException("En-tête obligatoire manquant : " + ENTETE_UTILISATEUR);
            }
            return Acteur.agent(identifiant);
        }
    }
}