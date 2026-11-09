package tg.nassara.estimation.entity.reference;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/** Villa, duplex, immeuble, appartement, etc. */
@Entity
@Table(name = "type_bien")
public class TypeBien extends ReferenceAvecCoefficient {

    protected TypeBien() {
    }

    public TypeBien(String code, String libelle, BigDecimal coefficient) {
        super(code, libelle, coefficient);
    }
}