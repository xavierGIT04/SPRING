package tg.nassara.estimation.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/** Quartier de Lomé : prix de base, coefficient appris et rendement local. Créable à la volée par un agent. */
@Entity
@Table(name = "quartier")
public class Quartier extends EntiteReference {

    /** Prix de base du terrain nu au m² (XOF). */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal prixBaseTerrainM2;

    /** Coefficient de zone (1,0000 = neutre), réajusté par l'apprentissage. */
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal coefficientZone = BigDecimal.ONE;

    /** Rendement local ; s'il est nul, le rendement par défaut de ParametreGlobal s'applique. */
    @Column(precision = 8, scale = 4)
    private BigDecimal tauxCapitalisation;

    /** Vrai tant que le prix de base a été saisi à chaud, sans validation. */
    @Column(nullable = false)
    private boolean prixBaseProvisoire = false;

    @Column(nullable = false)
    private int nombreTransactionsCalibrage = 0;

    protected Quartier() {
    }

    public Quartier(String code, String libelle, BigDecimal prixBaseTerrainM2) {
        super(code, libelle);
        this.prixBaseTerrainM2 = prixBaseTerrainM2;
    }

    public BigDecimal getPrixBaseTerrainM2() { return prixBaseTerrainM2; }
    public void setPrixBaseTerrainM2(BigDecimal prixBaseTerrainM2) { this.prixBaseTerrainM2 = prixBaseTerrainM2; }
    public BigDecimal getCoefficientZone() { return coefficientZone; }
    public void setCoefficientZone(BigDecimal coefficientZone) { this.coefficientZone = coefficientZone; }
    public BigDecimal getTauxCapitalisation() { return tauxCapitalisation; }
    public void setTauxCapitalisation(BigDecimal tauxCapitalisation) { this.tauxCapitalisation = tauxCapitalisation; }
    public boolean isPrixBaseProvisoire() { return prixBaseProvisoire; }
    public void setPrixBaseProvisoire(boolean prixBaseProvisoire) { this.prixBaseProvisoire = prixBaseProvisoire; }
    public int getNombreTransactionsCalibrage() { return nombreTransactionsCalibrage; }
    public void setNombreTransactionsCalibrage(int nombreTransactionsCalibrage) { this.nombreTransactionsCalibrage = nombreTransactionsCalibrage; }
}