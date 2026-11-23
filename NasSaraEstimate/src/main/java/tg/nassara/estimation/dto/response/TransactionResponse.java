package tg.nassara.estimation.dto.response;

import org.hibernate.Hibernate;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.entity.Estimation;
import tg.nassara.estimation.entity.TransactionImmobiliere;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.StatutTransaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;

/**
 * « ecartRelatif » = (prix réel − prix central estimé) ÷ prix central estimé : nul sans estimation préalable.
 * « integreApprentissage » : consommée par le coefficient de zone et le taux ; « integreCout » : par le coût de
 * construction. Une transaction consommée par l'un ou l'autre n'est plus annulable.
 * À construire dans une transaction ouverte (bien et estimation chargés à la demande).
 */
public record TransactionResponse(
        Long id,
        Long version,
        Long bienId,
        NatureBien nature,
        ReferenceResume quartier,
        Long estimationId,
        BigDecimal prixCentralEstime,
        BigDecimal prixVenteEffectif,
        BigDecimal ecartRelatif,
        BigDecimal loyerMensuelConstate,
        LocalDate dateTransaction,
        StatutTransaction statut,
        String validePar,
        Instant dateValidation,
        boolean integreApprentissage,
        boolean integreCout) {

    public static TransactionResponse depuis(TransactionImmobiliere t) {
        Bien bien = (Bien) Hibernate.unproxy(t.getBien());
        Estimation estimation = t.getEstimationPrealable() == null
                ? null : (Estimation) Hibernate.unproxy(t.getEstimationPrealable());
        BigDecimal central = estimation == null ? null : estimation.getPrixCentral();
        BigDecimal ecart = (central == null || central.signum() <= 0) ? null
                : t.getPrixVenteEffectif().subtract(central).divide(central, 4, RoundingMode.HALF_UP);
        return new TransactionResponse(t.getId(), t.getVersion(), bien.getId(), bien.nature(),
                ReferenceResume.de(bien.getQuartier()),
                estimation == null ? null : estimation.getId(), central,
                t.getPrixVenteEffectif(), ecart, t.getLoyerMensuelConstate(), t.getDateTransaction(),
                t.getStatut(), t.getValidePar(), t.getDateValidation(),
                t.isIntegreApprentissage(), t.isIntegreCout());
    }
}