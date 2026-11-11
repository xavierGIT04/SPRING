package tg.nassara.estimation.service.estimation;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import tg.nassara.estimation.dto.response.EstimationResponse;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.entity.Estimation;
import tg.nassara.estimation.entity.PonderationMethode;
import tg.nassara.estimation.entity.ResultatMethode;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.StatutEstimation;
import tg.nassara.estimation.exception.DonneesInsuffisantesException;
import tg.nassara.estimation.exception.EstimationImpossibleException;
import tg.nassara.estimation.exception.ParametreManquantException;
import tg.nassara.estimation.exception.RessourceIntrouvableException;
import tg.nassara.estimation.repository.BienRepository;
import tg.nassara.estimation.repository.EstimationRepository;
import tg.nassara.estimation.service.marche.ResultatSynchronisation;
import tg.nassara.estimation.service.marche.ServiceSynchronisationCatalogue;
import tg.nassara.estimation.service.parametrage.Acteur;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Orchestrateur : sélection des méthodes, exécution, fusion et persistance d'une estimation.
 *
 * <p>Déroulement :
 * <ol>
 *   <li>hors transaction : actualisation du catalogue (lecture HTTP, avec cache) ;</li>
 *   <li>dans UNE transaction : méthodes retenues = pondération active (poids &gt; 0) ET {@code estApplicable} ;
 *       exécution ; fusion ; enregistrement de {@link Estimation} et {@link ResultatMethode}.</li>
 * </ol>
 *
 * <p>Règles d'erreur : {@link ParametreManquantException} (y compris l'absence de toute pondération active) arrête
 * tout et rien n'est enregistré ; {@link DonneesInsuffisantesException} écarte la méthode (estimation PARTIELLE) ;
 * si plus aucune méthode ne conclut, {@link EstimationImpossibleException}. Une méthode non applicable au bien est
 * ignorée sans rendre l'estimation partielle (ex. capitalisation sur un bien non locatif).
 */
@Service
public class ServiceEstimation {

    private final BienRepository bienRepository;
    private final EstimationRepository estimationRepository;
    private final ParametresCalcul parametres;
    private final RegistreEstimateurs registre;
    private final FusionResultats fusion;
    private final ServiceSynchronisationCatalogue synchronisation;
    private final TransactionTemplate transaction;

    public ServiceEstimation(BienRepository bienRepository, EstimationRepository estimationRepository,
                             ParametresCalcul parametres, RegistreEstimateurs registre, FusionResultats fusion,
                             ServiceSynchronisationCatalogue synchronisation,
                             PlatformTransactionManager transactionManager) {
        this.bienRepository = bienRepository;
        this.estimationRepository = estimationRepository;
        this.parametres = parametres;
        this.registre = registre;
        this.fusion = fusion;
        this.synchronisation = synchronisation;
        this.transaction = new TransactionTemplate(transactionManager);
    }

    private record Execution(List<FusionResultats.Contribution> contributions, List<String> raisons,
                             boolean partielle) {
    }

    /** Volontairement SANS @Transactional : la lecture du catalogue ne doit pas garder une connexion ouverte. */
    public EstimationResponse estimer(Long bienId, Acteur acteur) {
        if (bienId == null || !bienRepository.existsById(bienId)) {
            throw new RessourceIntrouvableException("Bien introuvable : " + bienId);
        }
        ResultatSynchronisation synchro = synchronisation.synchroniserSiNecessaire();
        return transaction.execute(statut -> calculerEtEnregistrer(bienId, acteur, synchro));
    }

    private EstimationResponse calculerEtEnregistrer(Long bienId, Acteur acteur, ResultatSynchronisation synchro) {
        Bien bien = (Bien) Hibernate.unproxy(bienRepository.findById(bienId)
                .orElseThrow(() -> new RessourceIntrouvableException("Bien introuvable : " + bienId)));
        NatureBien nature = bien.nature();

        List<PonderationMethode> ponderations = parametres.ponderationsActives(nature).stream()
                .filter(p -> p.getPoids().signum() > 0)
                .sorted(Comparator.comparing(PonderationMethode::getMethode))
                .toList();
        if (ponderations.isEmpty()) {
            throw new ParametreManquantException("PonderationMethode", nature.name());
        }

        List<String> avertissements = new ArrayList<>();
        if (synchro != null && synchro.statut() == ResultatSynchronisation.Statut.ECHEC) {
            avertissements.add(synchro.message());
        }

        Execution execution = executer(bien, ponderations, avertissements);
        if (execution.contributions().isEmpty()) {
            throw new EstimationImpossibleException(execution.raisons());
        }

        FusionResultats.Resultat fusionne = fusion.fusionner(execution.contributions());
        avertissements.addAll(fusionne.avertissements());

        return enregistrer(bien, acteur, execution, fusionne, avertissements);
    }

    private Execution executer(Bien bien, List<PonderationMethode> ponderations, List<String> avertissements) {
        List<FusionResultats.Contribution> contributions = new ArrayList<>();
        List<String> raisons = new ArrayList<>();
        boolean partielle = false;

        for (PonderationMethode ponderation : ponderations) {
            Optional<Estimateur> trouve = registre.pour(ponderation.getMethode());
            if (trouve.isEmpty()) {
                String message = "Méthode " + ponderation.getMethode() + " activée mais aucun estimateur disponible.";
                avertissements.add(message);
                raisons.add(message);
                partielle = true;
                continue;
            }
            Estimateur estimateur = trouve.get();
            if (!estimateur.estApplicable(bien)) {
                String message = "Méthode " + ponderation.getMethode() + " non applicable à ce bien : ignorée.";
                avertissements.add(message);
                raisons.add(message);
                continue;
            }
            try {
                ResultatMethodeCalcule resultat = estimateur.estimer(bien);
                contributions.add(new FusionResultats.Contribution(
                        ponderation.getMethode(), ponderation.getPoids(), resultat));
                for (String avertissement : resultat.avertissements()) {
                    avertissements.add("[" + ponderation.getMethode() + "] " + avertissement);
                }
            } catch (DonneesInsuffisantesException e) {
                avertissements.add("Méthode écartée — " + e.getMessage());
                raisons.add(e.getMessage());
                partielle = true;
            }
        }
        return new Execution(contributions, raisons, partielle);
    }

    private EstimationResponse enregistrer(Bien bien, Acteur acteur, Execution execution,
                                           FusionResultats.Resultat fusionne, List<String> avertissements) {
        Estimation estimation = new Estimation();
        estimation.setBien(bien);
        estimation.setPrixMin(fusionne.prixMin());
        estimation.setPrixCentral(fusionne.prixCentral());
        estimation.setPrixMax(fusionne.prixMax());
        estimation.setIndiceConfiance(fusionne.indiceConfiance());
        estimation.setStatut(execution.partielle() ? StatutEstimation.PARTIELLE : StatutEstimation.CALCULEE);
        estimation.setDemandePar(acteur.identifiant());
        estimation.setAvertissements(avertissements.isEmpty() ? null : String.join("\n", avertissements));

        for (int i = 0; i < execution.contributions().size(); i++) {
            FusionResultats.Contribution contribution = execution.contributions().get(i);
            FusionResultats.Part part = fusionne.parts().get(i);
            ResultatMethodeCalcule r = contribution.resultat();

            Map<String, Object> persiste = new LinkedHashMap<>();
            persiste.put("poidsParametre", contribution.poidsParametre());
            persiste.put("partFusion", part.partFusion());
            persiste.put("avertissements", r.avertissements());
            persiste.put("calcul", r.detail());

            ResultatMethode resultat = new ResultatMethode();
            resultat.setMethode(contribution.methode());
            resultat.setPrixMin(r.prixMin());
            resultat.setPrixCentral(r.prixCentral());
            resultat.setPrixMax(r.prixMax());
            resultat.setIndiceConfiance(r.indiceConfiance());
            resultat.setPoidsApplique(part.partFusion());
            resultat.setDetailCalcul(JsonSimple.versJson(persiste));
            estimation.ajouterResultat(resultat);
        }

        // Même construction qu'à la consultation : la réponse reflète exactement ce qui est enregistré.
        return EstimationResponse.depuis(estimationRepository.save(estimation));
    }
}
