package tg.nassara.estimation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/** Paramètre numérique global identifié par une clé (rendement par défaut, seuils d'apprentissage, etc.). */
@Entity
@Table(name = "parametre_global")
public class ParametreGlobal extends EntiteBase {

    @Column(nullable = false, unique = true, length = 80)
    private String cle;

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal valeur;

    @Column(length = 30)
    private String unite;

    @Column(length = 255)
    private String description;

    protected ParametreGlobal() {
    }

    public ParametreGlobal(String cle, BigDecimal valeur, String unite, String description) {
        this.cle = cle;
        this.valeur = valeur;
        this.unite = unite;
        this.description = description;
    }

    public String getCle() { return cle; }
    public void setCle(String cle) { this.cle = cle; }
    public BigDecimal getValeur() { return valeur; }
    public void setValeur(BigDecimal valeur) { this.valeur = valeur; }
    public String getUnite() { return unite; }
    public void setUnite(String unite) { this.unite = unite; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}