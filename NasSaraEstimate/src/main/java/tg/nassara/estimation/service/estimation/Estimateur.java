package tg.nassara.estimation.service.estimation;

import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.enums.MethodeEstimation;

/**
 * Contrat commun des méthodes d'estimation. Une classe par méthode.
 *
 * <p>Règles pour les implémentations :
 * <ul>
 *   <li>sans état et thread-safe ; aucune persistance (c'est le rôle de l'orchestrateur) ;</li>
 *   <li>tout paramètre de calcul est lu via {@link ParametresCalcul}, jamais écrit en dur ;</li>
 *   <li>paramètre absent : laisser remonter {@code ParametreManquantException} (l'estimation s'arrête) ;</li>
 *   <li>données insuffisantes pour ce bien : lever {@code DonneesInsuffisantesException} (méthode écartée) ;</li>
 *   <li>ne jamais substituer silencieusement une valeur par défaut ;</li>
 *   <li>appelées dans une transaction ouverte par l'orchestrateur (les relations du bien sont chargées à la demande).</li>
 * </ul>
 */
public interface Estimateur {

    MethodeEstimation methode();

    /**
     * Applicabilité technique pour ce bien (ex. la capitalisation exige un bien locatif ou commercial).
     * L'activation par nature de bien, elle, est décidée par les pondérations paramétrées.
     */
    boolean estApplicable(Bien bien);

    ResultatMethodeCalcule estimer(Bien bien);
}