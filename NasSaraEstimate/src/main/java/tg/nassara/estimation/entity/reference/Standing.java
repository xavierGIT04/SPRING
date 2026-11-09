package tg.nassara.estimation.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/** Niveau de standing : coût de construction à neuf et paramètres de vétusté. */
@Entity
@Table(name = "standing")
public class Standing extends EntiteReference {

    /** Valeur de remplacement à neuf au m² bâti (XOF), réajustée par l'apprentissage. */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal coutConstructionM2;

    /** Vétusté linéaire annuelle (ex. 0,0200 = 2 % par an). */
    @Column(nullable = false, precision = 6, scale = 4)
    private BigDecimal tauxVetusteAnnuel;

    /** Plafond de vétusté (ex. 0,7000 = 70 % maximum de décote). */
    @Column(nullable = false, precision = 6, scale = 4)
    private BigDecimal plafondVetuste;

    protected Standing() {
    }

    public Standing(String code, String libelle, BigDecimal coutConstructionM2,
                    BigDecimal tauxVetusteAnnuel, BigDecimal plafondVetuste) {
        super(code, libelle);
        this.coutConstructionM2 = coutConstructionM2;
        this.tauxVetusteAnnuel = tauxVetusteAnnuel;
        this.plafondVetuste = plafondVetuste;
    }

    public BigDecimal getCoutConstructionM2() { return coutConstructionM2; }
    public void setCoutConstructionM2(BigDecimal coutConstructionM2) { this.coutConstructionM2 = coutConstructionM2; }
    public BigDecimal getTauxVetusteAnnuel() { return tauxVetusteAnnuel; }
    public void setTauxVetusteAnnuel(BigDecimal tauxVetusteAnnuel) { this.tauxVetusteAnnuel = tauxVetusteAnnuel; }
    public BigDecimal getPlafondVetuste() { return plafondVetuste; }
    public void setPlafondVetuste(BigDecimal plafondVetuste) { this.plafondVetuste = plafondVetuste; }
}