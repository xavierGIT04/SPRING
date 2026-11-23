package tg.nassara.estimation.service.apprentissage;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.nassara.estimation.dto.response.RecalibrageResponse;
import tg.nassara.estimation.dto.response.RecalibrageResponse.QuartierEnAttente;
import tg.nassara.estimation.dto.response.RecalibrageResponse.QuartierRecalibre;
import tg.nassara.estimation.dto.response.RecalibrageResponse.QuartierRendement;
import tg.nassara.estimation.dto.response.RecalibrageResponse.StandingEnAttente;
import tg.nassara.estimation.dto.response.RecalibrageResponse.StandingRecalibre;
import tg.nassara.estimation.dto.response.RecalibrageResponse.TransactionEcartee;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.entity.BienBati;
import tg.nassara.estimation.entity.Equipement;
import tg.nassara.estimation.entity.TransactionImmobiliere;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.entity.reference.Standing;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.StatutTransaction;
import tg.nassara.estimation.exception.DonneesInsuffisantesException;
import tg.nassara.estimation.exception.RessourceIntrouvableException;
import tg.nassara.estimation.repository.QuartierRepository;
import tg.nassara.estimation.repository.StandingRepository;
import tg.nassara.estimation.repository.TransactionRepository;
import tg.nassara.estimation.service.estimation.ClesParametres;
import tg.nassara.estimation.service.estimation.EstimateurHedonique;
import tg.nassara.estimation.service.estimation.ParametresCalcul;
import tg.nassara.estimation.service.estimation.ResultatMethodeCalcule;
import tg.nassara.estimation.service.parametrage.Acteur;
import tg.nassara.estimation.service.parametrage.ParametrageService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Apprentissage continu : recalibrage, à partir des transactions validées non encore intégrées,
 * (1) du coefficient de zone des quartiers, (2) de leur taux de capitalisation, (3) du coût de construction des standings.
 *
 * <p><b>Coefficient de zone</b> : la part TERRAIN du prix réel (prix − construction après vétusté − équipements) est
 * comparée à la valeur de terrain de la méthode hédonique ; par quartier : médiane des ratios, lissage exponentiel,
 * variation limitée par cycle, bornes absolues.
 *
 * <p><b>Coût de construction</b> : réservé aux ventes de bâtis dont le quartier est déjà bien calibré (au moins
 * {@code HEDONIQUE_TRANSACTIONS_CALIBRAGE_CIBLE} ventes intégrées au début du cycle). On isole la part CONSTRUCTION
 * (prix − terrain hédonique − équipements), on la ramène au m² bâti, au coefficient du type de bien et à la vétusté
 * pour obtenir un coût observé, comparé au coût actuel du standing. Drapeau d'intégration distinct
 * ({@code integreCout}) : une même vente peut nourrir les deux apprentissages, à des moments différents.
 *
 * <p>Toutes les transactions sont évaluées AVANT toute écriture (état du début de cycle), donc la simulation et le
 * recalibrage réel donnent les mêmes valeurs. Les écritures passent par {@link ParametrageService} (historisées,
 * origine APPRENTISSAGE, restaurables).
 *
 * <p>Limites connues : le coefficient de zone absorbe tout l'écart du terrain ; le coût de construction absorbe tout
 * l'écart de la construction (standing, vétusté, finitions comprises).
 */
@Service
public class RecalibrageService {

    private static final String TYPE_QUARTIERS = "quartiers";
    private static final String TYPE_STANDINGS = "standings";
    private static final int TAILLE_MAX_MOTIF = 500;

    private final TransactionRepository transactionRepository;
    private final QuartierRepository quartierRepository;
    private final StandingRepository standingRepository;
    private final EstimateurHedonique hedonique;
    private final ParametresCalcul parametres;
    private final ParametrageService parametrageService;

    public RecalibrageService(TransactionRepository transactionRepository, QuartierRepository quartierRepository,
                              StandingRepository standingRepository, EstimateurHedonique hedonique,
                              ParametresCalcul parametres, ParametrageService parametrageService) {
        this.transactionRepository = transactionRepository;
        this.quartierRepository = quartierRepository;
        this.standingRepository = standingRepository;
        this.hedonique = hedonique;
        this.parametres = parametres;
        this.parametrageService = parametrageService;
    }

    private record Evaluation(TransactionImmobiliere transaction, Quartier quartier, BigDecimal ratio,
                              BigDecimal tauxObserve) {
    }

    private record EvaluationCout(TransactionImmobiliere transaction, Standing standing, BigDecimal ratio,
                                  BigDecimal coutObserve) {
    }

    private record BilanCout(List<StandingRecalibre> recalibres, List<StandingEnAttente> enAttente,
                             int integrees, int enAttenteCalibrage) {
    }

    @Transactional
    public RecalibrageResponse recalibrer(boolean simulation, Acteur declencheur) {
        // ----- Paramètres (un paramètre manquant ou incohérent arrête tout) -----
        BigDecimal lissage = parametres.valeur(ClesParametres.APPRENTISSAGE_LISSAGE);
        int minimum = parametres.entier(ClesParametres.APPRENTISSAGE_MIN_TRANSACTIONS_QUARTIER);
        BigDecimal variationMax = parametres.valeur(ClesParametres.APPRENTISSAGE_VARIATION_MAX_CYCLE);
        BigDecimal coefMin = parametres.valeur(ClesParametres.APPRENTISSAGE_COEFFICIENT_ZONE_MIN);
        BigDecimal coefMax = parametres.valeur(ClesParametres.APPRENTISSAGE_COEFFICIENT_ZONE_MAX);
        verifier(lissage, minimum, variationMax, coefMin, coefMax);

        List<TransactionImmobiliere> enAttente = transactionRepository
                .findByStatutAndIntegreApprentissageFalse(StatutTransaction.VALIDEE);
        Map<Long, ResultatMethodeCalcule> cache = new HashMap<>();
        Set<Long> examinees = new HashSet<>();

        // ----- Évaluation (coefficient de zone et taux) AVANT toute écriture -----
        List<Evaluation> evaluations = new ArrayList<>();
        List<TransactionEcartee> ecartees = new ArrayList<>();
        for (TransactionImmobiliere transaction : enAttente) {
            examinees.add(transaction.getId());
            Bien bien = (Bien) Hibernate.unproxy(transaction.getBien());
            try {
                ResultatMethodeCalcule resultat = estimer(bien, cache);
                BigDecimal valeurTerrain = lire(resultat.detail(), "terrain", "valeurTerrain");
                BigDecimal construction = bien.nature() == NatureBien.BATI
                        ? lire(resultat.detail(), "construction", "valeurApresVetuste") : BigDecimal.ZERO;
                BigDecimal equipements = equipements(bien);
                BigDecimal partTerrain = transaction.getPrixVenteEffectif().subtract(construction).subtract(equipements);

                if (valeurTerrain.signum() <= 0) {
                    ecarter(ecartees, transaction.getId(), bien.getId(),
                            "Valeur de terrain hédonique nulle : transaction inexploitable.");
                    continue;
                }
                if (partTerrain.signum() <= 0) {
                    ecarter(ecartees, transaction.getId(), bien.getId(),
                            "Part terrain isolée nulle ou négative (prix réel "
                                    + transaction.getPrixVenteEffectif().toPlainString() + " XOF, construction "
                                    + construction.toPlainString() + " XOF, équipements "
                                    + equipements.toPlainString()
                                    + " XOF) : vérifier le prix, le standing ou la vétusté.");
                    continue;
                }
                BigDecimal ratio = partTerrain.divide(valeurTerrain, 4, RoundingMode.HALF_UP);
                evaluations.add(new Evaluation(transaction, bien.getQuartier(), ratio,
                        tauxObserve(bien, transaction)));
            } catch (DonneesInsuffisantesException e) {
                ecarter(ecartees, transaction.getId(), bien.getId(), e.getMessage());
            }
        }

        // ----- Évaluation du coût de construction (toujours avant toute écriture) -----
        List<EvaluationCout> evaluationsCout = new ArrayList<>();
        int enAttenteCalibrage = evaluerCouts(cache, ecartees, examinees, evaluationsCout);

        // ----- Regroupement par quartier -----
        Map<Long, List<Evaluation>> parQuartier = new LinkedHashMap<>();
        for (Evaluation evaluation : evaluations) {
            parQuartier.computeIfAbsent(evaluation.quartier().getId(), id -> new ArrayList<>()).add(evaluation);
        }

        // ----- Application par quartier : coefficient de zone et taux -----
        List<QuartierRecalibre> recalibres = new ArrayList<>();
        List<QuartierRendement> rendements = new ArrayList<>();
        List<QuartierEnAttente> quartiersEnAttente = new ArrayList<>();
        int integrees = 0;
        Instant maintenant = Instant.now();

        for (Map.Entry<Long, List<Evaluation>> entree : parQuartier.entrySet()) {
            Quartier quartier = quartierRepository.findById(entree.getKey())
                    .orElseThrow(() -> new RessourceIntrouvableException("Quartier introuvable : " + entree.getKey()));
            List<Evaluation> groupe = entree.getValue();

            if (groupe.size() < minimum) {
                quartiersEnAttente.add(new QuartierEnAttente(quartier.getId(), quartier.getCode(),
                        quartier.getLibelle(), groupe.size(), minimum));
                continue;
            }

            BigDecimal mediane = CalculRecalibrage.mediane(groupe.stream().map(Evaluation::ratio).toList());
            BigDecimal ancien = quartier.getCoefficientZone();
            CalculRecalibrage.MiseAJour miseAJour = CalculRecalibrage.nouveauCoefficient(
                    ancien, mediane, lissage, variationMax, coefMin, coefMax);

            QuartierRendement rendement = recalibrerRendement(quartier, groupe, minimum, lissage,
                    variationMax, simulation, declencheur);
            if (rendement != null) {
                rendements.add(rendement);
            }

            if (!simulation) {
                if (miseAJour.nouveau().compareTo(ancien) != 0) {
                    parametrageService.modifier(TYPE_QUARTIERS, quartier.getId(), null,
                            Map.of("coefficientZone", miseAJour.nouveau().toPlainString()),
                            motif(groupe.size(), mediane, lissage, miseAJour.limite(), declencheur),
                            Acteur.apprentissage());
                }
                quartier.setNombreTransactionsCalibrage(quartier.getNombreTransactionsCalibrage() + groupe.size());
                quartierRepository.save(quartier);
                for (Evaluation evaluation : groupe) {
                    TransactionImmobiliere transaction = evaluation.transaction();
                    transaction.setIntegreApprentissage(true);
                    transaction.setDateIntegrationApprentissage(maintenant);
                    transactionRepository.save(transaction);
                }
            }
            integrees += groupe.size();
            recalibres.add(new QuartierRecalibre(quartier.getId(), quartier.getCode(), quartier.getLibelle(),
                    ancien, miseAJour.nouveau(), mediane, groupe.size(), miseAJour.limite()));
        }

        // ----- Application par standing : coût de construction -----
        BilanCout cout = recalibrerCouts(evaluationsCout, enAttenteCalibrage, lissage, variationMax,
                simulation, declencheur, maintenant);

        return new RecalibrageResponse(simulation, maintenant, examinees.size(), integrees,
                cout.integrees(), cout.enAttenteCalibrage(),
                List.copyOf(recalibres), List.copyOf(rendements), cout.recalibres(),
                List.copyOf(quartiersEnAttente), cout.enAttente(), List.copyOf(ecartees));
    }

    // ---------- Coût de construction ----------

    /**
     * Évalue les ventes de bâtis non encore intégrées au coût. Retourne le nombre de ventes qui attendent que leur
     * quartier soit assez calibré (elles restent en attente, sans être signalées comme écartées).
     */
    private int evaluerCouts(Map<Long, ResultatMethodeCalcule> cache, List<TransactionEcartee> ecartees,
                             Set<Long> examinees, List<EvaluationCout> evaluationsCout) {
        List<TransactionImmobiliere> candidates = transactionRepository
                .findByStatutAndIntegreCoutFalse(StatutTransaction.VALIDEE).stream()
                .filter(t -> Hibernate.unproxy(t.getBien()) instanceof BienBati)
                .toList();
        if (candidates.isEmpty()) {
            return 0;
        }
        BigDecimal cible = parametres.valeur(ClesParametres.HEDONIQUE_TRANSACTIONS_CALIBRAGE_CIBLE);
        if (cible.signum() <= 0) {
            throw new IllegalStateException("Paramétrage incohérent : "
                    + ClesParametres.HEDONIQUE_TRANSACTIONS_CALIBRAGE_CIBLE + " doit être strictement positif ("
                    + cible + ").");
        }

        int enAttenteCalibrage = 0;
        for (TransactionImmobiliere transaction : candidates) {
            examinees.add(transaction.getId());
            BienBati bati = (BienBati) Hibernate.unproxy(transaction.getBien());
            if (BigDecimal.valueOf(bati.getQuartier().getNombreTransactionsCalibrage()).compareTo(cible) < 0) {
                enAttenteCalibrage++;
                continue;
            }
            EvaluationCout evaluation = evaluerCout(transaction, bati, cache, ecartees);
            if (evaluation != null) {
                evaluationsCout.add(evaluation);
            }
        }
        return enAttenteCalibrage;
    }

    private EvaluationCout evaluerCout(TransactionImmobiliere transaction, BienBati bati,
                                       Map<Long, ResultatMethodeCalcule> cache, List<TransactionEcartee> ecartees) {
        try {
            ResultatMethodeCalcule resultat = estimer(bati, cache);
            Map<String, Object> detail = resultat.detail();
            BigDecimal valeurTerrain = lire(detail, "terrain", "valeurTerrain");
            BigDecimal surface = lire(detail, "construction", "surfaceBatieM2");
            BigDecimal coefficientType = lire(detail, "construction", "coefficientTypeBien");
            BigDecimal vetuste = lire(detail, "construction", "vetusteRetenue");
            BigDecimal coutActuel = lire(detail, "construction", "coutConstructionM2");
            BigDecimal equipements = equipements(bati);

            BigDecimal partConstruction = transaction.getPrixVenteEffectif()
                    .subtract(valeurTerrain).subtract(equipements);
            if (partConstruction.signum() <= 0) {
                ecarter(ecartees, transaction.getId(), bati.getId(),
                        "Part construction isolée nulle ou négative (prix réel "
                                + transaction.getPrixVenteEffectif().toPlainString() + " XOF, terrain hédonique "
                                + valeurTerrain.toPlainString() + " XOF, équipements " + equipements.toPlainString()
                                + " XOF) : vérifier le prix, le prix de base du quartier ou le coefficient de zone.");
                return null;
            }
            BigDecimal denominateur = surface.multiply(coefficientType).multiply(BigDecimal.ONE.subtract(vetuste));
            if (denominateur.signum() <= 0 || coutActuel.signum() <= 0 || bati.getStanding() == null) {
                ecarter(ecartees, transaction.getId(), bati.getId(),
                        "Surface bâtie, coefficient du type de bien, vétusté ou standing inexploitable "
                                + "pour calculer un coût de construction au m².");
                return null;
            }
            BigDecimal coutObserve = partConstruction.divide(denominateur, 2, RoundingMode.HALF_UP);
            BigDecimal ratio = coutObserve.divide(coutActuel, 6, RoundingMode.HALF_UP);
            Standing standing = (Standing) Hibernate.unproxy(bati.getStanding());
            return new EvaluationCout(transaction, standing, ratio, coutObserve);
        } catch (DonneesInsuffisantesException e) {
            ecarter(ecartees, transaction.getId(), bati.getId(), e.getMessage());
            return null;
        }
    }

    private BilanCout recalibrerCouts(List<EvaluationCout> evaluations, int enAttenteCalibrage,
                                      BigDecimal lissage, BigDecimal variationMax, boolean simulation,
                                      Acteur declencheur, Instant maintenant) {
        if (evaluations.isEmpty()) {
            return new BilanCout(List.of(), List.of(), 0, enAttenteCalibrage);
        }
        int minimum = parametres.entier(ClesParametres.APPRENTISSAGE_MIN_TRANSACTIONS_STANDING);
        BigDecimal coutMin = parametres.valeur(ClesParametres.APPRENTISSAGE_COUT_CONSTRUCTION_MIN);
        BigDecimal coutMax = parametres.valeur(ClesParametres.APPRENTISSAGE_COUT_CONSTRUCTION_MAX);
        verifierCouts(minimum, coutMin, coutMax);

        Map<Long, List<EvaluationCout>> parStanding = new LinkedHashMap<>();
        for (EvaluationCout evaluation : evaluations) {
            parStanding.computeIfAbsent(evaluation.standing().getId(), id -> new ArrayList<>()).add(evaluation);
        }

        List<StandingRecalibre> recalibres = new ArrayList<>();
        List<StandingEnAttente> enAttente = new ArrayList<>();
        int integrees = 0;

        for (Map.Entry<Long, List<EvaluationCout>> entree : parStanding.entrySet()) {
            Standing standing = standingRepository.findById(entree.getKey())
                    .orElseThrow(() -> new RessourceIntrouvableException("Standing introuvable : " + entree.getKey()));
            List<EvaluationCout> groupe = entree.getValue();

            if (groupe.size() < minimum) {
                enAttente.add(new StandingEnAttente(standing.getId(), standing.getCode(), standing.getLibelle(),
                        groupe.size(), minimum));
                continue;
            }

            BigDecimal ratioMedian = CalculRecalibrage.mediane(groupe.stream().map(EvaluationCout::ratio).toList());
            BigDecimal coutObserveMedian = CalculRecalibrage.mediane(
                    groupe.stream().map(EvaluationCout::coutObserve).toList());
            BigDecimal ancien = standing.getCoutConstructionM2();
            CalculRecalibrage.MiseAJour miseAJour = CalculRecalibrage.nouveauCoefficient(
                    ancien, ratioMedian, lissage, variationMax, coutMin, coutMax);
            BigDecimal nouveau = miseAJour.nouveau().setScale(0, RoundingMode.HALF_UP); // le franc CFA n'a pas de centimes

            if (!simulation) {
                if (nouveau.compareTo(ancien) != 0) {
                    parametrageService.modifier(TYPE_STANDINGS, standing.getId(), null,
                            Map.of("coutConstructionM2", nouveau.toPlainString()),
                            motifCout(groupe.size(), ratioMedian, coutObserveMedian, miseAJour.limite(), declencheur),
                            Acteur.apprentissage());
                }
                standing.setNombreTransactionsCalibrage(standing.getNombreTransactionsCalibrage() + groupe.size());
                standingRepository.save(standing);
                for (EvaluationCout evaluation : groupe) {
                    TransactionImmobiliere transaction = evaluation.transaction();
                    transaction.setIntegreCout(true);
                    transaction.setDateIntegrationCout(maintenant);
                    transactionRepository.save(transaction);
                }
            }
            integrees += groupe.size();
            recalibres.add(new StandingRecalibre(standing.getId(), standing.getCode(), standing.getLibelle(),
                    ancien, nouveau, coutObserveMedian, ratioMedian, groupe.size(), miseAJour.limite()));
        }
        return new BilanCout(List.copyOf(recalibres), List.copyOf(enAttente), integrees, enAttenteCalibrage);
    }

    // ---------- Taux de capitalisation ----------

    /** Taux observé = loyer annuel net ÷ prix de vente ; nul sans loyer constaté ou hors bâti locatif. */
    private BigDecimal tauxObserve(Bien bien, TransactionImmobiliere transaction) {
        if (!(bien instanceof BienBati bati) || !bati.isLocatifOuCommercial()) {
            return null;
        }
        BigDecimal loyer = transaction.getLoyerMensuelConstate();
        if (loyer == null || loyer.signum() <= 0) {
            return null;
        }
        BigDecimal charges = parametres.valeur(ClesParametres.CAPITALISATION_TAUX_CHARGES);
        if (charges.signum() < 0 || charges.compareTo(BigDecimal.ONE) >= 0) {
            throw new IllegalStateException("Paramétrage incohérent : " + ClesParametres.CAPITALISATION_TAUX_CHARGES
                    + " doit être compris entre 0 (inclus) et 1 (exclu) (" + charges + ").");
        }
        return loyer.multiply(BigDecimal.valueOf(12)).multiply(BigDecimal.ONE.subtract(charges))
                .divide(transaction.getPrixVenteEffectif(), 6, RoundingMode.HALF_UP);
    }

    private QuartierRendement recalibrerRendement(Quartier quartier, List<Evaluation> groupe, int minimum,
                                                  BigDecimal lissage, BigDecimal variationMax,
                                                  boolean simulation, Acteur declencheur) {
        List<BigDecimal> observes = groupe.stream().map(Evaluation::tauxObserve).filter(Objects::nonNull).toList();
        if (observes.size() < minimum) {
            return null; // pas assez de loyers constatés : le taux n'est pas touché
        }
        BigDecimal tauxMin = parametres.valeur(ClesParametres.APPRENTISSAGE_TAUX_CAPITALISATION_MIN);
        BigDecimal tauxMax = parametres.valeur(ClesParametres.APPRENTISSAGE_TAUX_CAPITALISATION_MAX);
        if (tauxMin.signum() <= 0 || tauxMin.compareTo(tauxMax) > 0 || tauxMax.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("Paramétrage incohérent : il faut 0 < "
                    + ClesParametres.APPRENTISSAGE_TAUX_CAPITALISATION_MIN + " (" + tauxMin + ") <= "
                    + ClesParametres.APPRENTISSAGE_TAUX_CAPITALISATION_MAX + " (" + tauxMax + ") <= 1.");
        }
        boolean parDefaut = quartier.getTauxCapitalisation() == null;
        BigDecimal ancien = parDefaut
                ? parametres.valeur(ClesParametres.CAPITALISATION_TAUX_DEFAUT) : quartier.getTauxCapitalisation();
        BigDecimal mediane = CalculRecalibrage.mediane(observes);
        BigDecimal ratio = mediane.divide(ancien, 6, RoundingMode.HALF_UP);
        CalculRecalibrage.MiseAJour miseAJour = CalculRecalibrage.nouveauCoefficient(
                ancien, ratio, lissage, variationMax, tauxMin, tauxMax);

        if (!simulation && miseAJour.nouveau().compareTo(ancien) != 0) {
            String texte = "Recalibrage du taux de capitalisation : " + observes.size()
                    + " vente(s) avec loyer constaté, taux observé médian "
                    + mediane.setScale(4, RoundingMode.HALF_UP).toPlainString()
                    + (parDefaut ? ", base : taux par défaut" : "")
                    + (miseAJour.limite() != null ? ", borné (" + miseAJour.limite() + ")" : "")
                    + ". Déclenché par " + declencheur.identifiant() + ".";
            parametrageService.modifier(TYPE_QUARTIERS, quartier.getId(), null,
                    Map.of("tauxCapitalisation", miseAJour.nouveau().toPlainString()),
                    texte.length() > TAILLE_MAX_MOTIF ? texte.substring(0, TAILLE_MAX_MOTIF) : texte,
                    Acteur.apprentissage());
        }
        return new QuartierRendement(quartier.getId(), quartier.getCode(), quartier.getLibelle(),
                ancien, parDefaut, miseAJour.nouveau(), mediane, observes.size(), miseAJour.limite());
    }

    // ---------- Utilitaires ----------

    /** Une seule estimation hédonique par bien et par cycle (zone et coût lisent le même détail). */
    private ResultatMethodeCalcule estimer(Bien bien, Map<Long, ResultatMethodeCalcule> cache) {
        if (bien.getId() == null) {
            return hedonique.estimer(bien);
        }
        ResultatMethodeCalcule connu = cache.get(bien.getId());
        if (connu != null) {
            return connu;
        }
        ResultatMethodeCalcule resultat = hedonique.estimer(bien);
        cache.put(bien.getId(), resultat);
        return resultat;
    }

    private static BigDecimal equipements(Bien bien) {
        return bien.getEquipements().stream()
                .map(Equipement::montantRetenu)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Une même raison n'est signalée qu'une fois par transaction (elle peut apparaître aux deux apprentissages). */
    private static void ecarter(List<TransactionEcartee> ecartees, Long transactionId, Long bienId, String raison) {
        boolean dejaSignalee = ecartees.stream()
                .anyMatch(e -> Objects.equals(e.transactionId(), transactionId) && e.raison().equals(raison));
        if (!dejaSignalee) {
            ecartees.add(new TransactionEcartee(transactionId, bienId, raison));
        }
    }

    private static void verifier(BigDecimal lissage, int minimum, BigDecimal variationMax,
                                 BigDecimal coefMin, BigDecimal coefMax) {
        if (lissage.signum() <= 0 || lissage.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("Paramétrage incohérent : " + ClesParametres.APPRENTISSAGE_LISSAGE
                    + " doit être compris entre 0 (exclu) et 1 (" + lissage + ").");
        }
        if (minimum < 1) {
            throw new IllegalStateException("Paramétrage incohérent : "
                    + ClesParametres.APPRENTISSAGE_MIN_TRANSACTIONS_QUARTIER + " doit être >= 1 (" + minimum + ").");
        }
        if (variationMax.signum() <= 0 || variationMax.compareTo(BigDecimal.ONE) >= 0) {
            throw new IllegalStateException("Paramétrage incohérent : " + ClesParametres.APPRENTISSAGE_VARIATION_MAX_CYCLE
                    + " doit être compris entre 0 (exclu) et 1 (exclu) (" + variationMax + ").");
        }
        if (coefMin.signum() <= 0 || coefMin.compareTo(coefMax) > 0) {
            throw new IllegalStateException("Paramétrage incohérent : " + ClesParametres.APPRENTISSAGE_COEFFICIENT_ZONE_MIN
                    + " (" + coefMin + ") doit être > 0 et <= " + ClesParametres.APPRENTISSAGE_COEFFICIENT_ZONE_MAX
                    + " (" + coefMax + ").");
        }
    }

    private static void verifierCouts(int minimum, BigDecimal coutMin, BigDecimal coutMax) {
        if (minimum < 1) {
            throw new IllegalStateException("Paramétrage incohérent : "
                    + ClesParametres.APPRENTISSAGE_MIN_TRANSACTIONS_STANDING + " doit être >= 1 (" + minimum + ").");
        }
        if (coutMin.signum() <= 0 || coutMin.compareTo(coutMax) > 0) {
            throw new IllegalStateException("Paramétrage incohérent : "
                    + ClesParametres.APPRENTISSAGE_COUT_CONSTRUCTION_MIN + " (" + coutMin + ") doit être > 0 et <= "
                    + ClesParametres.APPRENTISSAGE_COUT_CONSTRUCTION_MAX + " (" + coutMax + ").");
        }
    }

    /**
     * Lecture du détail du calcul hédonique. Si sa structure change, l'échec est volontairement bruyant
     * plutôt qu'un recalibrage sur des valeurs fausses.
     */
    private static BigDecimal lire(Map<String, Object> detail, String section, String cle) {
        if (detail.get(section) instanceof Map<?, ?> bloc && bloc.get(cle) instanceof BigDecimal valeur) {
            return valeur;
        }
        throw new IllegalStateException("Détail hédonique inattendu : « " + section + "." + cle + " » absent.");
    }

    private static String motif(int nombre, BigDecimal mediane, BigDecimal lissage, String limite, Acteur declencheur) {
        String texte = "Recalibrage : " + nombre + " vente(s) validée(s), ratio médian "
                + mediane.stripTrailingZeros().toPlainString() + ", lissage "
                + lissage.stripTrailingZeros().toPlainString()
                + (limite != null ? ", borné (" + limite + ")" : "")
                + ". Déclenché par " + declencheur.identifiant() + ".";
        return texte.length() > TAILLE_MAX_MOTIF ? texte.substring(0, TAILLE_MAX_MOTIF) : texte;
    }

    private static String motifCout(int nombre, BigDecimal ratioMedian, BigDecimal coutObserveMedian,
                                    String limite, Acteur declencheur) {
        String texte = "Recalibrage du coût de construction : " + nombre
                + " vente(s) de biens bâtis (quartiers calibrés), ratio médian coût observé / coût actuel "
                + ratioMedian.stripTrailingZeros().toPlainString() + ", coût observé médian "
                + coutObserveMedian.setScale(0, RoundingMode.HALF_UP).toPlainString() + " XOF/m²"
                + (limite != null ? ", borné (" + limite + ")" : "")
                + ". Déclenché par " + declencheur.identifiant() + ".";
        return texte.length() > TAILLE_MAX_MOTIF ? texte.substring(0, TAILLE_MAX_MOTIF) : texte;
    }
}