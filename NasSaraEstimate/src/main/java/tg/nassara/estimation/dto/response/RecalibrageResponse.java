package tg.nassara.estimation.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Bilan d'un recalibrage. En simulation, rien n'est écrit : le bilan montre ce qui serait fait.
 * « limite » : null, ou la raison pour laquelle la valeur a été bornée (VARIATION_MAX, BORNE_MIN, BORNE_MAX).
 *
 * @param transactionsIntegrees               transactions consommées par le coefficient de zone et le taux
 * @param transactionsCoutIntegrees           transactions consommées par le coût de construction
 * @param transactionsCoutEnAttenteCalibrage  ventes de bâtis dont le quartier n'est pas encore assez calibré
 *                                            pour apprendre le coût de construction
 */
public record RecalibrageResponse(
        boolean simulation,
        Instant date,
        int transactionsExaminees,
        int transactionsIntegrees,
        int transactionsCoutIntegrees,
        int transactionsCoutEnAttenteCalibrage,
        List<QuartierRecalibre> quartiers,
        List<QuartierRendement> rendements,
        List<StandingRecalibre> standings,
        List<QuartierEnAttente> quartiersEnAttente,
        List<StandingEnAttente> standingsEnAttente,
        List<TransactionEcartee> transactionsEcartees) {

    public record QuartierRecalibre(Long quartierId, String code, String libelle,
                                    BigDecimal ancienCoefficient, BigDecimal nouveauCoefficient,
                                    BigDecimal ratioMedian, int transactionsUtilisees, String limite) {
    }

    /** « ancienTauxParDefaut » : vrai si le quartier n'avait pas son propre taux (le taux global servait de base). */
    public record QuartierRendement(Long quartierId, String code, String libelle,
                                    BigDecimal ancienTaux, boolean ancienTauxParDefaut, BigDecimal nouveauTaux,
                                    BigDecimal tauxObserveMedian, int transactionsUtilisees, String limite) {
    }

    /** Coûts en XOF par m² bâti ; « ratioMedian » = coût observé ÷ coût actuel du standing. */
    public record StandingRecalibre(Long standingId, String code, String libelle,
                                    BigDecimal ancienCout, BigDecimal nouveauCout,
                                    BigDecimal coutObserveMedian, BigDecimal ratioMedian,
                                    int transactionsUtilisees, String limite) {
    }

    public record QuartierEnAttente(Long quartierId, String code, String libelle,
                                    int transactionsDisponibles, int minimumRequis) {
    }

    public record StandingEnAttente(Long standingId, String code, String libelle,
                                    int transactionsDisponibles, int minimumRequis) {
    }

    public record TransactionEcartee(Long transactionId, Long bienId, String raison) {
    }
}