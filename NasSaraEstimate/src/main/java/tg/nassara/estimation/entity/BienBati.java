package tg.nassara.estimation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import tg.nassara.estimation.entity.reference.Standing;
import tg.nassara.estimation.entity.reference.TypeBien;
import tg.nassara.estimation.enums.NatureBien;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("BATI")
public class BienBati extends Bien {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_bien_id")
    private TypeBien typeBien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standing_id")
    private Standing standing;

    @Column(precision = 12, scale = 2)
    private BigDecimal surfaceBatieM2;

    private Integer anneeConstruction;

    private Integer nombreNiveaux;

    /** Vrai si le bien est locatif ou commercial : la capitalisation devient activable. */
    @Column(nullable = false)
    private boolean locatifOuCommercial;

    @Column(precision = 15, scale = 2)
    private BigDecimal loyerMensuelActuel;

    @Override
    public NatureBien nature() {
        return NatureBien.BATI;
    }

    public TypeBien getTypeBien() { return typeBien; }
    public void setTypeBien(TypeBien typeBien) { this.typeBien = typeBien; }
    public Standing getStanding() { return standing; }
    public void setStanding(Standing standing) { this.standing = standing; }
    public BigDecimal getSurfaceBatieM2() { return surfaceBatieM2; }
    public void setSurfaceBatieM2(BigDecimal surfaceBatieM2) { this.surfaceBatieM2 = surfaceBatieM2; }
    public Integer getAnneeConstruction() { return anneeConstruction; }
    public void setAnneeConstruction(Integer anneeConstruction) { this.anneeConstruction = anneeConstruction; }
    public Integer getNombreNiveaux() { return nombreNiveaux; }
    public void setNombreNiveaux(Integer nombreNiveaux) { this.nombreNiveaux = nombreNiveaux; }
    public boolean isLocatifOuCommercial() { return locatifOuCommercial; }
    public void setLocatifOuCommercial(boolean locatifOuCommercial) { this.locatifOuCommercial = locatifOuCommercial; }
    public BigDecimal getLoyerMensuelActuel() { return loyerMensuelActuel; }
    public void setLoyerMensuelActuel(BigDecimal loyerMensuelActuel) { this.loyerMensuelActuel = loyerMensuelActuel; }
}