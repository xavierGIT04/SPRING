package tg.nassara.estimation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tg.nassara.estimation.enums.MethodeEstimation;

import java.math.BigDecimal;

/** Résultat d'une méthode pour une estimation : traçabilité complète des paramètres utilisés. */
@Entity
@Table(name = "resultat_methode")
public class ResultatMethode extends EntiteBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estimation_id", nullable = false)
    private Estimation estimation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MethodeEstimation methode;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal prixMin;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal prixCentral;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal prixMax;

    @Column(precision = 5, scale = 4)
    private BigDecimal indiceConfiance;

    /** Poids appliqué lors de la fusion (copie de la pondération en vigueur à ce moment-là). */
    @Column(precision = 8, scale = 4)
    private BigDecimal poidsApplique;

    /** Détail du calcul en JSON : valeurs de paramètres utilisées, comparables retenus, etc. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String detailCalcul;

    public Estimation getEstimation() { return estimation; }
    public void setEstimation(Estimation estimation) { this.estimation = estimation; }
    public MethodeEstimation getMethode() { return methode; }
    public void setMethode(MethodeEstimation methode) { this.methode = methode; }
    public BigDecimal getPrixMin() { return prixMin; }
    public void setPrixMin(BigDecimal prixMin) { this.prixMin = prixMin; }
    public BigDecimal getPrixCentral() { return prixCentral; }
    public void setPrixCentral(BigDecimal prixCentral) { this.prixCentral = prixCentral; }
    public BigDecimal getPrixMax() { return prixMax; }
    public void setPrixMax(BigDecimal prixMax) { this.prixMax = prixMax; }
    public BigDecimal getIndiceConfiance() { return indiceConfiance; }
    public void setIndiceConfiance(BigDecimal indiceConfiance) { this.indiceConfiance = indiceConfiance; }
    public BigDecimal getPoidsApplique() { return poidsApplique; }
    public void setPoidsApplique(BigDecimal poidsApplique) { this.poidsApplique = poidsApplique; }
    public String getDetailCalcul() { return detailCalcul; }
    public void setDetailCalcul(String detailCalcul) { this.detailCalcul = detailCalcul; }
}