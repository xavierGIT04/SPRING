package tg.nassara.estimation.entity.reference;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/** Titre Foncier, convention de vente, etc. */
@Entity
@Table(name = "statut_juridique")
public class StatutJuridique extends ReferenceAvecCoefficient {

    protected StatutJuridique() {
    }

    public StatutJuridique(String code, String libelle, BigDecimal coefficient) {
        super(code, libelle, coefficient);
    }
}