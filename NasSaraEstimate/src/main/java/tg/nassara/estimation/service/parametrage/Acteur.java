package tg.nassara.estimation.service.parametrage;

import tg.nassara.estimation.enums.OrigineModification;
import tg.nassara.estimation.exception.DonneesInvalidesException;

/** Auteur d'une modification : un agent identifié par l'appelant, ou le processus d'apprentissage. */
public record Acteur(String identifiant, OrigineModification origine) {

    public Acteur {
        if (identifiant == null || identifiant.isBlank()) {
            throw new DonneesInvalidesException("L'identité de l'utilisateur est obligatoire.");
        }
    }

    public static Acteur agent(String identifiant) {
        return new Acteur(identifiant == null ? null : identifiant.strip(), OrigineModification.AGENT);
    }

    public static Acteur apprentissage() {
        return new Acteur("systeme-apprentissage", OrigineModification.APPRENTISSAGE);
    }
}