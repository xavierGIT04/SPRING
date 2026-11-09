package tg.nassara.estimation.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.math.BigDecimal;

/** Référence valorisée par un coefficient multiplicatif (voie, topographie, statut juridique, etc.). */
@MappedSuperclass
public abstract class ReferenceAvecCoefficient extends EntiteReference {

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal coefficient;

    protected ReferenceAvecCoefficient() {
    }

    protected ReferenceAvecCoefficient(String code, String libelle, BigDecimal coefficient) {
        super(code, libelle);
        this.coefficient = coefficient;
    }

    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
}