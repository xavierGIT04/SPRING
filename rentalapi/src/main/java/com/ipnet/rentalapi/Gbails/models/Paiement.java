package com.ipnet.rentalapi.Gbails.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ipnet.rentalapi.Gbails.Enums.ModePaiement;

import jakarta.persistence.*;


@Entity
@Table(name = "paiements")
public class Paiement {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid = UUID.randomUUID();

    @Column(name = "date_paiement", nullable = false)
    private LocalDateTime datePaiement; // Date et heure de l'encaissement

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant; // Somme versée

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false)
    private ModePaiement modePaiement;

    @Column(name = "reference_paiement")
    private String referencePaiement; // Ex: ID transaction T-Money/Flooz ou Numéro de chèque

    @Column(name = "recu_url", nullable = true)
    private String recuUrl; // Lien vers la quittance de loyer au format PDF si générée

    // RELATIONS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "echeance_id", nullable = false)
    private Echeance echeance;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public LocalDateTime getDatePaiement() {
		return datePaiement;
	}

	public void setDatePaiement(LocalDateTime datePaiement) {
		this.datePaiement = datePaiement;
	}

	public BigDecimal getMontant() {
		return montant;
	}

	public void setMontant(BigDecimal montant) {
		this.montant = montant;
	}

	public ModePaiement getModePaiement() {
		return modePaiement;
	}

	public void setModePaiement(ModePaiement modePaiement) {
		this.modePaiement = modePaiement;
	}

	public String getReferencePaiement() {
		return referencePaiement;
	}

	public void setReferencePaiement(String referencePaiement) {
		this.referencePaiement = referencePaiement;
	}

	public String getRecuUrl() {
		return recuUrl;
	}

	public void setRecuUrl(String recuUrl) {
		this.recuUrl = recuUrl;
	}

	public Echeance getEcheance() {
		return echeance;
	}

	public void setEcheance(Echeance echeance) {
		this.echeance = echeance;
	}

	public Paiement() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
    
}
