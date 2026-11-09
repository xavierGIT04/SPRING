package tg.nassara.estimation.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import tg.nassara.estimation.entity.EntiteBase;

/** Valeur de référence gérée par les agents : code stable, libellé, désactivable (jamais supprimée). */
@MappedSuperclass
public abstract class EntiteReference extends EntiteBase {

    @Column(nullable = false, unique = true, length = 60)
    private String code;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column(nullable = false)
    private boolean actif = true;

    protected EntiteReference() {
    }

    protected EntiteReference(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}