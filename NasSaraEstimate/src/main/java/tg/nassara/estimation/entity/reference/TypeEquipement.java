package tg.nassara.estimation.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/** Catalogue extensible : forage, solaire, piscine, clôture sécurisée, gardien, etc. */
@Entity
@Table(name = "type_equipement")
public class TypeEquipement extends EntiteReference {

    /** Montant de référence en XOF, utilisé quand l'équipement d'un bien n'a pas de montant propre. */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montantReference;

    protected TypeEquipement() {
    }

    public TypeEquipement(String code, String libelle, BigDecimal montantReference) {
        super(code, libelle);
        this.montantReference = montantReference;
    }

    public BigDecimal getMontantReference() { return montantReference; }
    public void setMontantReference(BigDecimal montantReference) { this.montantReference = montantReference; }
}