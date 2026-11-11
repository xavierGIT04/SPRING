package tg.nassara.estimation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.entity.reference.TypeBien;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.OrigineAnnonce;
import tg.nassara.estimation.enums.TypeAnnonce;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Annonce servant à la comparaison de marché : lue sur le catalogue de l'agence ou saisie par un agent.
 * Pour une vente, la surface retenue pour comparer est celle du terrain.
 */
@Entity
@Table(name = "annonce_marche")
public class AnnonceMarche extends EntiteBase {

    /** Origine lisible : « CATALOGUE_NAS_SARRA », ou le site / la provenance indiquée par l'agent. */
    @Column(nullable = false, length = 80)
    private String source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrigineAnnonce origine;

    /** URL unique (évite les doublons) ; facultative pour une saisie manuelle sans lien. */
    @Column(length = 600, unique = true)
    private String urlSource;

    @Column(length = 255)
    private String titre;

    /** Identifiant de l'agent pour une saisie manuelle. */
    @Column(length = 120)
    private String saisiePar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeAnnonce typeAnnonce;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NatureBien nature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_bien_id")
    private TypeBien typeBien;

    /** Quartier rapproché ; nul si le quartier de l'annonce n'a pas pu être identifié. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quartier_id")
    private Quartier quartier;

    @Column(length = 150)
    private String quartierBrut;

    @Column(precision = 12, scale = 2)
    private BigDecimal surfaceTerrainM2;

    @Column(precision = 12, scale = 2)
    private BigDecimal surfaceBatieM2;

    /** Prix demandé (vente) ou loyer mensuel demandé (location), en XOF. */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    private LocalDate datePublication;

    /** Date de dernière vérification : pour le catalogue, mise à jour à chaque synchronisation tant que l'annonce est en ligne. */
    @Column(nullable = false)
    private LocalDate dateCollecte;

    @Column(nullable = false)
    private boolean actif = true;

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public OrigineAnnonce getOrigine() { return origine; }
    public void setOrigine(OrigineAnnonce origine) { this.origine = origine; }
    public String getUrlSource() { return urlSource; }
    public void setUrlSource(String urlSource) { this.urlSource = urlSource; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getSaisiePar() { return saisiePar; }
    public void setSaisiePar(String saisiePar) { this.saisiePar = saisiePar; }
    public TypeAnnonce getTypeAnnonce() { return typeAnnonce; }
    public void setTypeAnnonce(TypeAnnonce typeAnnonce) { this.typeAnnonce = typeAnnonce; }
    public NatureBien getNature() { return nature; }
    public void setNature(NatureBien nature) { this.nature = nature; }
    public TypeBien getTypeBien() { return typeBien; }
    public void setTypeBien(TypeBien typeBien) { this.typeBien = typeBien; }
    public Quartier getQuartier() { return quartier; }
    public void setQuartier(Quartier quartier) { this.quartier = quartier; }
    public String getQuartierBrut() { return quartierBrut; }
    public void setQuartierBrut(String quartierBrut) { this.quartierBrut = quartierBrut; }
    public BigDecimal getSurfaceTerrainM2() { return surfaceTerrainM2; }
    public void setSurfaceTerrainM2(BigDecimal surfaceTerrainM2) { this.surfaceTerrainM2 = surfaceTerrainM2; }
    public BigDecimal getSurfaceBatieM2() { return surfaceBatieM2; }
    public void setSurfaceBatieM2(BigDecimal surfaceBatieM2) { this.surfaceBatieM2 = surfaceBatieM2; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public LocalDate getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDate datePublication) { this.datePublication = datePublication; }
    public LocalDate getDateCollecte() { return dateCollecte; }
    public void setDateCollecte(LocalDate dateCollecte) { this.dateCollecte = dateCollecte; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}
