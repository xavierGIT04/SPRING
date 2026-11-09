package tg.nassara.estimation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import tg.nassara.estimation.enums.MethodeEstimation;
import tg.nassara.estimation.enums.NatureBien;

import java.math.BigDecimal;

/** Poids d'une méthode dans la fusion finale, par nature de bien. Les poids sont normalisés à l'exécution. */
@Entity
@Table(name = "ponderation_methode",
        uniqueConstraints = @UniqueConstraint(columnNames = {"methode", "nature_bien"}))
public class PonderationMethode extends EntiteBase {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MethodeEstimation methode;

    @Enumerated(EnumType.STRING)
    @Column(name = "nature_bien", nullable = false, length = 20)
    private NatureBien natureBien;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal poids;

    /** Permet à un agent de neutraliser une méthode sans supprimer sa pondération. */
    @Column(nullable = false)
    private boolean active = true;

    protected PonderationMethode() {
    }

    public PonderationMethode(MethodeEstimation methode, NatureBien natureBien, BigDecimal poids) {
        this.methode = methode;
        this.natureBien = natureBien;
        this.poids = poids;
    }

    public MethodeEstimation getMethode() { return methode; }
    public void setMethode(MethodeEstimation methode) { this.methode = methode; }
    public NatureBien getNatureBien() { return natureBien; }
    public void setNatureBien(NatureBien natureBien) { this.natureBien = natureBien; }
    public BigDecimal getPoids() { return poids; }
    public void setPoids(BigDecimal poids) { this.poids = poids; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}