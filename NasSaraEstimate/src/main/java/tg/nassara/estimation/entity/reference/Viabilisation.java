package tg.nassara.estimation.entity.reference;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/** Raccordement valorisé : CEET, TdE, etc. Un bien peut en cumuler plusieurs. */
@Entity
@Table(name = "viabilisation")
public class Viabilisation extends ReferenceAvecCoefficient {

    protected Viabilisation() {
    }

    public Viabilisation(String code, String libelle, BigDecimal coefficient) {
        super(code, libelle, coefficient);
    }
}