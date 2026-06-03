package com.wake.GreenBank.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

@Entity
public class Client {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private UUID publicId = UUID.randomUUID();
	
	@Column(length = 56, nullable = false)
	private String nom;
	
	@Column(length = 30, nullable = false)
	private String email;
	
	@Column(nullable = false)
	private Float revenuAnnuel;
	
	@OneToMany(mappedBy = "client")
	private List<Pret> lesprets = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public List<Pret> getLesprets() {
		return lesprets;
	}

	public void setLesprets(List<Pret> lesprets) {
		this.lesprets = lesprets;
	}

	public UUID getPublicId() {
		return publicId;
	}

	public void setPublicId(UUID publicId) {
		this.publicId = publicId;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Float getRevenuAnnuel() {
		return revenuAnnuel;
	}

	public void setRevenuAnnuel(Float revenuAnnuel) {
		this.revenuAnnuel = revenuAnnuel;
	}

	public Client() {
		super();
		
	}
	
	
	
}
