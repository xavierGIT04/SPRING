package tg.nassara.estimation.entity.reference;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/** Goudron, pavé, piste, etc. */
@Entity
@Table(name = "type_voie")
public class TypeVoie extends ReferenceAvecCoefficient {

    protected TypeVoie() {
    }

    public TypeVoie(String code, String libelle, BigDecimal coefficient) {
        super(code, libelle, coefficient);
    }
}