package tg.nassara.estimation.service.apprentissage;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.nassara.estimation.dto.response.RecalibrageResponse;
import tg.nassara.estimation.dto.response.RecalibrageResponse.QuartierEnAttente;
import tg.nassara.estimation.dto.response.RecalibrageResponse.QuartierRecalibre;
import tg.nassara.estimation.dto.response.RecalibrageResponse.TransactionEcartee;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.entity.Equipement;
import tg.nassara.estimation.entity.TransactionImmobiliere;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.StatutTransaction;
import tg.nassara.estimation.exception.DonneesInsuffisantesException;
import tg.nassara.estimation.exception.RessourceIntrouvableException;
import tg.nassara.estimation.repository.QuartierRepository;
import tg.nassara.estimation.repository.TransactionRepository;
import tg.nassara.estimation.service.estimation.ClesParametres;
import tg.nassara.estimation.service.estimation.EstimateurHedonique;
import tg.nassara.estimation.service.estimation.ParametresCalcul;
import tg.nassara.estimation.service.estimation.ResultatMethodeCalcule;
import tg.nassara.estimation.service.parametrage.Acteur;
import tg.nassara.estimation.service.parametrage.ParametrageService;
import tg.nassara.estimation.dto.response.RecalibrageResponse.QuartierRendement;
import tg.nassara.estimation.entity.BienBati;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Apprentissage continu, volet 1 : recalibrage du coefficient de zone des quartiers à partir des transactions
 * validées non encore intégrées.
 *
 * <p>Pour chaque transaction, la part TERRAIN du prix réel (prix − construction après vétusté − équipements) est
 * comparée à la valeur de terrain de la méthode hédonique. Par quartier : médiane des ratios, lissage exponentiel,
 * variation limitée par cycle, bornes absolues. L'écriture passe par {@link ParametrageService} (historisée,
 * origine APPRENTISSAGE, restaurable).
 *
 * <p>Limite connue : le coefficient de zone absorbe tout l'écart du terrain (prix de base du quartier, coefficients
 * de site compris), pas seulement l'effet « zone ».
 */
@Service
public class RecalibrageService {

    private static final String TYPE_QUARTIERS = "quartiers";
    private static final int TAILLE_MAX_MOTIF = 500;

    private final TransactionRepository transactionRepository;
    private final QuartierRepository quartierRepository;
    private final EstimateurHedonique hedonique;
    private final ParametresCalcul parametres;
    private final ParametrageService parametrageService;

    public RecalibrageService(TransactionRepository transactionRepository, QuartierRepository quartierRepository,
                              EstimateurHedonique hedonique, ParametresCalcul parametres,
                              ParametrageService parametrageService) {
        this.transactionRepository = transactionRepository;
        this.quartierRepository = quartierRepository;
        this.hedonique = hedonique;
        this.parametres = parametres;
        this.parametrageService = parametrageService;
    }

    private record Evaluation(TransactionImmobiliere transaction, Quartier quartier, BigDecimal ratio,
            BigDecimal tauxObserve) {
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

        // ----- Évaluation de toutes les transactions AVANT toute écriture (coefficients du début de cycle) -----
        List<Evaluation> evaluations = new ArrayList<>();
        List<TransactionEcartee> ecartees = new ArrayList<>();
        for (TransactionImmobiliere transaction : enAttente) {
            Bien bien = (Bien) Hibernate.unproxy(transaction.getBien());
            try {
                ResultatMethodeCalcule resultat = hedonique.estimer(bien);
                BigDecimal valeurTerrain = lire(resultat.detail(), "terrain", "valeurTerrain");
                BigDecimal construction = bien.nature() == NatureBien.BATI
                        ? lire(resultat.detail(), "construction", "valeurApresVetuste") : BigDecimal.ZERO;
                BigDecimal equipements = bien.getEquipements().stream()
                        .map(Equipement::montantRetenu)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal partTerrain = transaction.getPrixVenteEffectif().subtract(construction).subtract(equipements);

                if (valeurTerrain.signum() <= 0) {
                    ecartees.add(new TransactionEcartee(transaction.getId(), bien.getId(),
                            "Valeur de terrain hédonique nulle : transaction inexploitable."));
                    continue;
                }
                if (partTerrain.signum() <= 0) {
                    ecartees.add(new TransactionEcartee(transaction.getId(), bien.getId(),
                            "Part terrain isolée nulle ou négative (prix réel " + transaction.getPrixVenteEffectif()
                                    .toPlainString() + " XOF, construction " + construction.toPlainString()
                                    + " XOF, équipements " + equipements.toPlainString()
                                    + " XOF) : vérifier le prix, le standing ou la vétusté."));
                    continue;
                }
                BigDecimal ratio = partTerrain.divide(valeurTerrain, 4, RoundingMode.HALF_UP);
                evaluations.add(new Evaluation(transaction, bien.getQuartier(), ratio,
                        tauxObserve(bien, transaction)));
            } catch (DonneesInsuffisantesException e) {
                ecartees.add(new TransactionEcartee(transaction.getId(), bien.getId(), e.getMessage()));
            }
        }

        // ----- Regroupement par quartier -----
        Map<Long, List<Evaluation>> parQuartier = new LinkedHashMap<>();
        for (Evaluation evaluation : evaluations) {
            parQuartier.computeIfAbsent(evaluation.quartier().getId(), id -> new ArrayList<>()).add(evaluation);
        }

        // ----- Application par quartier -----
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

        return new RecalibrageResponse(simulation, maintenant, enAttente.size(), integrees,
                List.copyOf(recalibres), List.copyOf(rendements), List.copyOf(quartiersEnAttente),
                List.copyOf(ecartees));
    }

    // ---------- Utilitaires ----------

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
}