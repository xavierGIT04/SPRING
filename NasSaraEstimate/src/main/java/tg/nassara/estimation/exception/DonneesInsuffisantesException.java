package tg.nassara.estimation.exception;

import tg.nassara.estimation.enums.MethodeEstimation;

/** Une méthode ne peut pas conclure pour ce bien (ex. trop peu de comparables) : elle est écartée, pas l'estimation. */
public class DonneesInsuffisantesException extends RuntimeException {

    private final MethodeEstimation methode;

    public DonneesInsuffisantesException(MethodeEstimation methode, String raison) {
        super(methode + " : " + raison);
        this.methode = methode;
    }

    public MethodeEstimation getMethode() { return methode; }
}