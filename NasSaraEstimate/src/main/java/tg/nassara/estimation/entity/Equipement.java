package tg.nassara.estimation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import tg.nassara.estimation.entity.reference.TypeEquipement;

import java.math.BigDecimal;

/** Équipement concret d'un bien : il appartient à ce seul bien. */
@Entity
@Table(name = "equipement")
public class Equipement extends EntiteBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bien_id", nullable = false)
    private Bien bien;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_equipement_id", nullable = false)
    private TypeEquipement typeEquipement;

    /** Montant propre à cet équipement (XOF) ; s'il est nul, le montant de référence du catalogue s'applique. */
    @Column(precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(length = 255)
    private String description;

    protected Equipement() {
    }

    public Equipement(TypeEquipement typeEquipement, BigDecimal montant, String description) {
        this.typeEquipement = typeEquipement;
        this.montant = montant;
        this.description = description;
    }

    /** Montant à retenir dans les calculs (nécessite une session ouverte : le type est chargé à la demande). */
    public BigDecimal montantRetenu() {
        return montant != null ? montant : typeEquipement.getMontantReference();
    }

    public Bien getBien() { return bien; }
    public void setBien(Bien bien) { this.bien = bien; }
    public TypeEquipement getTypeEquipement() { return typeEquipement; }
    public void setTypeEquipement(TypeEquipement typeEquipement) { this.typeEquipement = typeEquipement; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}