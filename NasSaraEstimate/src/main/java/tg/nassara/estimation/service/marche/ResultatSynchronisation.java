package tg.nassara.estimation.service.marche;

import java.time.Instant;

public record ResultatSynchronisation(
        Statut statut,
        int annoncesLues,
        int annoncesRetenues,
        int annoncesDesactivees,
        Instant date,
        String message) {

    public enum Statut {
        ACTUALISEE, CACHE_VALIDE, ECHEC
    }
}
