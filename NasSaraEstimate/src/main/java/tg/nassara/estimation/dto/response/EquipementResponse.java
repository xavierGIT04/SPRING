package tg.nassara.estimation.dto.response;

import tg.nassara.estimation.entity.Equipement;

import java.math.BigDecimal;

/** « montantRetenu » : montant propre, sinon montant du catalogue. À construire dans une transaction ouverte. */
public record EquipementResponse(Long id, ReferenceResume typeEquipement, BigDecimal montant,
                                 BigDecimal montantRetenu, String description) {

    public static EquipementResponse depuis(Equipement e) {
        return new EquipementResponse(e.getId(), ReferenceResume.de(e.getTypeEquipement()), e.getMontant(),
                e.montantRetenu(), e.getDescription());
    }
}
