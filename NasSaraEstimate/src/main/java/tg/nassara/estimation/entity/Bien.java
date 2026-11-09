package tg.nassara.estimation.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.entity.reference.StatutJuridique;
import tg.nassara.estimation.entity.reference.Topographie;
import tg.nassara.estimation.entity.reference.TypeVoie;
import tg.nassara.estimation.entity.reference.Viabilisation;
import tg.nassara.estimation.enums.NatureBien;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Socle commun du bien à estimer (terrain nu ou bâti). */
@Entity
@Table(name = "bien")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "nature", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class Bien extends EntiteBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quartier_id", nullable = false)
    private Quartier quartier;

    @Column(length = 255)
    private String adresse;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal surfaceTerrainM2;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_voie_id", nullable = false)
    private TypeVoie typeVoie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topographie_id", nullable = false)
    private Topographie topographie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "statut_juridique_id", nullable = false)
    private StatutJuridique statutJuridique;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "bien_viabilisation",
            joinColumns = @JoinColumn(name = "bien_id"),
            inverseJoinColumns = @JoinColumn(name = "viabilisation_id"))
    private Set<Viabilisation> viabilisations = new HashSet<>();

    /** Équipements propres à ce bien (cycle de vie lié au bien). */
    @OneToMany(mappedBy = "bien", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Equipement> equipements = new ArrayList<>();

    /** Nature technique du bien, déterminée par la sous-classe. */
    public abstract NatureBien nature();

    public void ajouterEquipement(Equipement equipement) {
        equipement.setBien(this);
        equipements.add(equipement);
    }

    public void retirerEquipement(Equipement equipement) {
        equipements.remove(equipement);
        equipement.setBien(null);
    }

    public Quartier getQuartier() { return quartier; }
    public void setQuartier(Quartier quartier) { this.quartier = quartier; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public BigDecimal getSurfaceTerrainM2() { return surfaceTerrainM2; }
    public void setSurfaceTerrainM2(BigDecimal surfaceTerrainM2) { this.surfaceTerrainM2 = surfaceTerrainM2; }
    public TypeVoie getTypeVoie() { return typeVoie; }
    public void setTypeVoie(TypeVoie typeVoie) { this.typeVoie = typeVoie; }
    public Topographie getTopographie() { return topographie; }
    public void setTopographie(Topographie topographie) { this.topographie = topographie; }
    public StatutJuridique getStatutJuridique() { return statutJuridique; }
    public void setStatutJuridique(StatutJuridique statutJuridique) { this.statutJuridique = statutJuridique; }
    public Set<Viabilisation> getViabilisations() { return viabilisations; }
    public void setViabilisations(Set<Viabilisation> viabilisations) { this.viabilisations = viabilisations; }
    public List<Equipement> getEquipements() { return equipements; }
}