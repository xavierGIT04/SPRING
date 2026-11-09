package tg.nassara.estimation.service.estimation;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import tg.nassara.estimation.enums.MethodeEstimation;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Annuaire des estimateurs disponibles, un par méthode. */
@Component
public class RegistreEstimateurs {

    private final Map<MethodeEstimation, Estimateur> parMethode = new EnumMap<>(MethodeEstimation.class);

    /** ObjectProvider : l'application démarre même tant qu'aucun estimateur n'est encore codé (étapes 4 à 6). */
    public RegistreEstimateurs(ObjectProvider<Estimateur> estimateurs) {
        estimateurs.orderedStream().forEach(estimateur -> {
            Estimateur precedent = parMethode.put(estimateur.methode(), estimateur);
            if (precedent != null) {
                throw new IllegalStateException("Deux estimateurs déclarés pour la méthode " + estimateur.methode());
            }
        });
    }

    public Optional<Estimateur> pour(MethodeEstimation methode) {
        return Optional.ofNullable(parMethode.get(methode));
    }

    public Set<MethodeEstimation> disponibles() {
        return parMethode.keySet();
    }
}