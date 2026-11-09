package tg.nassara.estimation.exception;

import java.util.List;

/** Aucune méthode n'a pu produire d'estimation pour ce bien. */
public class EstimationImpossibleException extends RuntimeException {

    private final List<String> raisons;

    public EstimationImpossibleException(List<String> raisons) {
        super("Aucune méthode n'a pu produire d'estimation : " + String.join(" | ", raisons));
        this.raisons = List.copyOf(raisons);
    }

    public List<String> getRaisons() { return raisons; }
}