package tg.nassara.estimation.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import tg.nassara.estimation.enums.StatutEstimation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Estimation finale : fourchette fusionnée et détail par méthode. */
@Entity
@Table(name = "estimation")
public class Estimation extends EntiteBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bien_id", nullable = false)
    private Bien bien;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal prixMin;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal prixCentral;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal prixMax;

    /** Indice global de confiance entre 0 et 1. */
    @Column(precision = 5, scale = 4)
    private BigDecimal indiceConfiance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutEstimation statut = StatutEstimation.CALCULEE;

    @Column(length = 120)
    private String demandePar;

    /** Limites constatées au calcul : méthode ignorée, données insuffisantes, prix de base provisoire, etc. */
    @Column(columnDefinition = "text")
    private String avertissements;

    @OneToMany(mappedBy = "estimation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResultatMethode> resultats = new ArrayList<>();

    public void ajouterResultat(ResultatMethode resultat) {
        resultat.setEstimation(this);
        resultats.add(resultat);
    }

    public Bien getBien() { return bien; }
    public void setBien(Bien bien) { this.bien = bien; }
    public BigDecimal getPrixMin() { return prixMin; }
    public void setPrixMin(BigDecimal prixMin) { this.prixMin = prixMin; }
    public BigDecimal getPrixCentral() { return prixCentral; }
    public void setPrixCentral(BigDecimal prixCentral) { this.prixCentral = prixCentral; }
    public BigDecimal getPrixMax() { return prixMax; }
    public void setPrixMax(BigDecimal prixMax) { this.prixMax = prixMax; }
    public BigDecimal getIndiceConfiance() { return indiceConfiance; }
    public void setIndiceConfiance(BigDecimal indiceConfiance) { this.indiceConfiance = indiceConfiance; }
    public StatutEstimation getStatut() { return statut; }
    public void setStatut(StatutEstimation statut) { this.statut = statut; }
    public String getDemandePar() { return demandePar; }
    public void setDemandePar(String demandePar) { this.demandePar = demandePar; }
    public String getAvertissements() { return avertissements; }
    public void setAvertissements(String avertissements) { this.avertissements = avertissements; }
    public List<ResultatMethode> getResultats() { return resultats; }
}