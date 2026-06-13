package com.ipnet.rentalapi.Gbails.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ipnet.rentalapi.Gbails.Enums.BailEnum;
import com.ipnet.rentalapi.Glogement.models.Unite;
import com.ipnet.rentalapi.auth.model.Utilisateur;

import jakarta.persistence.*;


@Entity
@Table(name = "contrats_bail")
@EntityListeners(AuditingEntityListener.class)
public class ContratBail {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_sortie")
    private LocalDate dateSortie; // Optionnel (pour les baux à durée déterminée)

    @Column(nullable = false)
    private Integer duree; // En mois (0 si durée indéterminée)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal loyer; 

    @Column(name = "jour_echeance", nullable = false)
    private Integer jourEcheance; // Ex: 5 (pour un paiement avant le 5 du mois)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BailEnum statut = BailEnum.ACTIF;

    @Column(columnDefinition = "TEXT")
    private String conditions; 
    
    @Column(name = "contrat_url")
    private String contratUrl; 

    // RELATIONS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locataire_id", nullable = false)
    private Utilisateur locataire; // L'utilisateur qui loue

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unite_id", nullable = false)
    private Unite unite; 

    @OneToMany(mappedBy = "contratBail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Echeance> echeances;
    
    
    
	public ContratBail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public LocalDate getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(LocalDate dateDebut) {
		this.dateDebut = dateDebut;
	}

	public LocalDate getDateSortie() {
		return dateSortie;
	}

	public void setDateSortie(LocalDate dateSortie) {
		this.dateSortie = dateSortie;
	}

	public Integer getDuree() {
		return duree;
	}

	public void setDuree(Integer duree) {
		this.duree = duree;
	}

	public BigDecimal getLoyer() {
		return loyer;
	}

	public void setLoyer(BigDecimal loyer) {
		this.loyer = loyer;
	}

	public Integer getJourEcheance() {
		return jourEcheance;
	}

	public void setJourEcheance(Integer jourEcheance) {
		this.jourEcheance = jourEcheance;
	}

	public BailEnum getStatut() {
		return statut;
	}

	public void setStatut(BailEnum statut) {
		this.statut = statut;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public Utilisateur getLocataire() {
		return locataire;
	}

	public void setLocataire(Utilisateur locataire) {
		this.locataire = locataire;
	}

	public Unite getUnite() {
		return unite;
	}

	public void setUnite(Unite unite) {
		this.unite = unite;
	}

	public List<Echeance> getEcheances() {
		return echeances;
	}

	public void setEcheances(List<Echeance> echeances) {
		this.echeances = echeances;
	}

	public String getContratUrl() {
		return contratUrl;
	}

	public void setContratUrl(String contratUrl) {
		this.contratUrl = contratUrl;
	}
    
    
}
