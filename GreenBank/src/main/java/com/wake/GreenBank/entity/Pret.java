package com.wake.GreenBank.entity;

import java.util.UUID;

import com.wake.GreenBank.enums.Statut;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Pret {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private UUID publicId = UUID.randomUUID();
	
	@Column
	private Float montant;
	
	@Column
	private int dureeMois;
	
	@Column
	private Statut statut ;
	
	@ManyToOne
	@JoinColumn(name = "client_id")
	private Client client;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Float getMontant() {
		return montant;
	}

	public void setMontant(Float montant) {
		this.montant = montant;
	}

	public int getDureeMois() {
		return dureeMois;
	}

	public void setDureeMois(int dureeMois) {
		this.dureeMois = dureeMois;
	}

	public Statut getStatut() {
		return statut;
	}

	public UUID getPublicId() {
		return publicId;
	}

	public void setPublicId(UUID publicId) {
		this.publicId = publicId;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public void setStatut(Statut statut) {
		this.statut = statut;
	}

	public Pret() {
		super();
	}
	
	

}
