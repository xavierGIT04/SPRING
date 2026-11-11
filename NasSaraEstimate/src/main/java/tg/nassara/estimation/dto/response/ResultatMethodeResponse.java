package tg.nassara.estimation.dto.response;

import com.fasterxml.jackson.annotation.JsonRawValue;
import tg.nassara.estimation.entity.ResultatMethode;
import tg.nassara.estimation.enums.MethodeEstimation;

import java.math.BigDecimal;

/**
 * Résultat d'une méthode dans une estimation enregistrée.
 * « detail » est le JSON conservé en base, renvoyé tel quel (non échappé) :
 * { poidsParametre, partFusion, avertissements, calcul : { comparables retenus, prix au m², loyers, etc. } }.
 */
public record ResultatMethodeResponse(
        MethodeEstimation methode,
        BigDecimal prixMin,
        BigDecimal prixCentral,
        BigDecimal prixMax,
        BigDecimal indiceConfiance,
        BigDecimal partFusion,
        @JsonRawValue String detail) {

    public static ResultatMethodeResponse depuis(ResultatMethode r) {
        return new ResultatMethodeResponse(r.getMethode(), r.getPrixMin(), r.getPrixCentral(), r.getPrixMax(),
                r.getIndiceConfiance(), r.getPoidsApplique(), r.getDetailCalcul());
    }
}
