package com.ipnet.rentalapi.Gbails.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.ipnet.rentalapi.Gbails.Enums.StatutEcheance;

import jakarta.persistence.*;

@Entity
@Table(name = "echeances")
public class Echeance {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(name = "date_echeance", nullable = false)
    private LocalDate dateEcheance; // Date limite de paiement (ex: 05/06/2026)

    @Column(name = "montant_du", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantDu; // Le loyer total à payer pour ce mois

    @Column(name = "montant_restant", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantRestant; // Ce qu'il reste à payer (0 si payé)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEcheance statut = StatutEcheance.EN_ATTENTE;

    // RELATIONS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_bail_id", nullable = false)
    private ContratBail contratBail;

    @OneToMany(mappedBy = "echeance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Paiement> paiements;

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

	public LocalDate getDateEcheance() {
		return dateEcheance;
	}

	public void setDateEcheance(LocalDate dateEcheance) {
		this.dateEcheance = dateEcheance;
	}

	public BigDecimal getMontantDu() {
		return montantDu;
	}

	public void setMontantDu(BigDecimal montantDu) {
		this.montantDu = montantDu;
	}

	public BigDecimal getMontantRestant() {
		return montantRestant;
	}

	public void setMontantRestant(BigDecimal montantRestant) {
		this.montantRestant = montantRestant;
	}

	public StatutEcheance getStatut() {
		return statut;
	}

	public void setStatut(StatutEcheance statut) {
		this.statut = statut;
	}

	public ContratBail getContratBail() {
		return contratBail;
	}

	public void setContratBail(ContratBail contratBail) {
		this.contratBail = contratBail;
	}

	public List<Paiement> getPaiements() {
		return paiements;
	}

	public void setPaiements(List<Paiement> paiements) {
		this.paiements = paiements;
	}

	public Echeance() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
    
}
