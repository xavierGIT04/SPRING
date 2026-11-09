package tg.nassara.estimation.exception;

/** Un paramètre de calcul n'a pas été renseigné par les agents : l'estimation ne doit pas continuer. */
public class ParametreManquantException extends RuntimeException {

    private final String typeParametre;
    private final String cle;

    public ParametreManquantException(String typeParametre, String cle) {
        super("Paramètre manquant : " + typeParametre + " « " + cle
                + " ». Renseignez-le dans l'administration des paramètres.");
        this.typeParametre = typeParametre;
        this.cle = cle;
    }

    public String getTypeParametre() { return typeParametre; }
    public String getCle() { return cle; }
}