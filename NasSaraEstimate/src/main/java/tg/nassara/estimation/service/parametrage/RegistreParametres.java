package tg.nassara.estimation.service.parametrage;

import org.springframework.stereotype.Component;
import tg.nassara.estimation.exception.RessourceIntrouvableException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Annuaire de toutes les définitions de paramètres déclarées dans l'application. */
@Component
public class RegistreParametres {

    private final Map<String, DefinitionParametre<?>> parType = new LinkedHashMap<>();
    private final Map<String, DefinitionParametre<?>> parNomEntite = new LinkedHashMap<>();

    public RegistreParametres(List<DefinitionParametre<?>> definitions) {
        for (DefinitionParametre<?> definition : definitions) {
            parType.put(definition.type(), definition);
            parNomEntite.put(definition.nomEntite(), definition);
        }
    }

    public DefinitionParametre<?> parType(String type) {
        DefinitionParametre<?> definition = parType.get(type);
        if (definition == null) {
            throw new RessourceIntrouvableException("Type de paramètre inconnu : " + type);
        }
        return definition;
    }

    public DefinitionParametre<?> parNomEntite(String nomEntite) {
        DefinitionParametre<?> definition = parNomEntite.get(nomEntite);
        if (definition == null) {
            throw new RessourceIntrouvableException("Type de paramètre inconnu dans l'historique : " + nomEntite);
        }
        return definition;
    }

    public Collection<DefinitionParametre<?>> toutes() {
        return parType.values();
    }
}