package tg.nassara.estimation.dto.response;

import tg.nassara.estimation.entity.reference.EntiteReference;

/** Identification lisible d'une valeur de référence. */
public record ReferenceResume(Long id, String code, String libelle) {

    public static ReferenceResume de(EntiteReference reference) {
        return reference == null ? null
                : new ReferenceResume(reference.getId(), reference.getCode(), reference.getLibelle());
    }
}
