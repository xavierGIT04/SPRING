package tg.nassara.estimation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import tg.nassara.estimation.enums.OrigineModification;
import tg.nassara.estimation.enums.TypeOperation;

import java.time.Instant;

/**
 * Journal append-only des modifications de paramètres : aucune mise à jour ni suppression.
 * Une restauration crée une nouvelle ligne (RESTAURATION) qui référence la ligne d'origine.
 */
@Entity
@Table(name = "historique_parametre",
        indexes = @Index(name = "idx_historique_parametre", columnList = "type_parametre,id_parametre"))
public class HistoriqueParametre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom de l'entité modifiée (ex. "Quartier", "Standing"). */
    @Column(name = "type_parametre", nullable = false, updatable = false, length = 60)
    private String typeParametre;

    @Column(name = "id_parametre", nullable = false, updatable = false)
    private Long idParametre;

    /** Code lisible du paramètre (ex. code du quartier), pour l'affichage sans jointure. */
    @Column(updatable = false, length = 150)
    private String referenceParametre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 20)
    private TypeOperation operation;

    @Column(updatable = false, length = 60)
    private String champ;

    @Column(updatable = false, length = 1000)
    private String ancienneValeur;

    @Column(updatable = false, length = 1000)
    private String nouvelleValeur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 20)
    private OrigineModification origine;

    @Column(nullable = false, updatable = false, length = 120)
    private String modifiePar;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant dateModification;

    @Column(updatable = false, length = 500)
    private String motif;

    /** Renseigné pour une RESTAURATION : identifiant de la ligne d'historique rétablie. */
    @Column(updatable = false)
    private Long restaureDepuisId;

    protected HistoriqueParametre() {
    }

    public HistoriqueParametre(String typeParametre, Long idParametre, String referenceParametre,
                               TypeOperation operation, String champ, String ancienneValeur,
                               String nouvelleValeur, OrigineModification origine, String modifiePar,
                               String motif, Long restaureDepuisId) {
        this.typeParametre = typeParametre;
        this.idParametre = idParametre;
        this.referenceParametre = referenceParametre;
        this.operation = operation;
        this.champ = champ;
        this.ancienneValeur = ancienneValeur;
        this.nouvelleValeur = nouvelleValeur;
        this.origine = origine;
        this.modifiePar = modifiePar;
        this.motif = motif;
        this.restaureDepuisId = restaureDepuisId;
    }

    public Long getId() { return id; }
    public String getTypeParametre() { return typeParametre; }
    public Long getIdParametre() { return idParametre; }
    public String getReferenceParametre() { return referenceParametre; }
    public TypeOperation getOperation() { return operation; }
    public String getChamp() { return champ; }
    public String getAncienneValeur() { return ancienneValeur; }
    public String getNouvelleValeur() { return nouvelleValeur; }
    public OrigineModification getOrigine() { return origine; }
    public String getModifiePar() { return modifiePar; }
    public Instant getDateModification() { return dateModification; }
    public String getMotif() { return motif; }
    public Long getRestaureDepuisId() { return restaureDepuisId; }
}