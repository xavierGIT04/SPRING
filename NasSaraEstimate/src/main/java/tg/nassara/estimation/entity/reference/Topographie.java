package tg.nassara.estimation.entity.reference;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/** Terrain plat, zone inondable, pente, etc. */
@Entity
@Table(name = "topographie")
public class Topographie extends ReferenceAvecCoefficient {

    protected Topographie() {
    }

    public Topographie(String code, String libelle, BigDecimal coefficient) {
        super(code, libelle, coefficient);
    }
}