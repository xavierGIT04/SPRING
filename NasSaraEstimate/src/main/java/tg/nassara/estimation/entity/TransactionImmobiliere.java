package tg.nassara.estimation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import tg.nassara.estimation.enums.StatutTransaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** Vérité terrain : transaction réelle de l'agence, source de l'apprentissage continu. */
@Entity
@Table(name = "transaction_immobiliere")
public class TransactionImmobiliere extends EntiteBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bien_id", nullable = false)
    private Bien bien;

    /** Estimation faite avant la vente : permet de mesurer l'écart estimation / réalité. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estimation_id")
    private Estimation estimationPrealable;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal prixVenteEffectif;

    /** Loyer mensuel constaté pour un bien locatif (calibrage du rendement). */
    @Column(precision = 15, scale = 2)
    private BigDecimal loyerMensuelConstate;

    @Column(nullable = false)
    private LocalDate dateTransaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutTransaction statut = StatutTransaction.EN_COURS;

    @Column(length = 120)
    private String validePar;

    private Instant dateValidation;

    /** Vrai une fois la transaction consommée par le recalibrage (évite le double comptage). */
    @Column(nullable = false)
    private boolean integreApprentissage = false;

    private Instant dateIntegrationApprentissage;

    public Bien getBien() { return bien; }
    public void setBien(Bien bien) { this.bien = bien; }
    public Estimation getEstimationPrealable() { return estimationPrealable; }
    public void setEstimationPrealable(Estimation estimationPrealable) { this.estimationPrealable = estimationPrealable; }
    public BigDecimal getPrixVenteEffectif() { return prixVenteEffectif; }
    public void setPrixVenteEffectif(BigDecimal prixVenteEffectif) { this.prixVenteEffectif = prixVenteEffectif; }
    public BigDecimal getLoyerMensuelConstate() { return loyerMensuelConstate; }
    public void setLoyerMensuelConstate(BigDecimal loyerMensuelConstate) { this.loyerMensuelConstate = loyerMensuelConstate; }
    public LocalDate getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(LocalDate dateTransaction) { this.dateTransaction = dateTransaction; }
    public StatutTransaction getStatut() { return statut; }
    public void setStatut(StatutTransaction statut) { this.statut = statut; }
    public String getValidePar() { return validePar; }
    public void setValidePar(String validePar) { this.validePar = validePar; }
    public Instant getDateValidation() { return dateValidation; }
    public void setDateValidation(Instant dateValidation) { this.dateValidation = dateValidation; }
    public boolean isIntegreApprentissage() { return integreApprentissage; }
    public void setIntegreApprentissage(boolean integreApprentissage) { this.integreApprentissage = integreApprentissage; }
    public Instant getDateIntegrationApprentissage() { return dateIntegrationApprentissage; }
    public void setDateIntegrationApprentissage(Instant dateIntegrationApprentissage) { this.dateIntegrationApprentissage = dateIntegrationApprentissage; }
}