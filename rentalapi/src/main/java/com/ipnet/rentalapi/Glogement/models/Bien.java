package com.ipnet.rentalapi.Glogement.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ipnet.rentalapi.Glogement.Enums.BienEnum;
import com.ipnet.rentalapi.auth.model.Utilisateur;
import com.ipnet.rentalapi.utils.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Bien extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private UUID uuid = UUID.randomUUID();
	
	private String libelle;
	
	@Enumerated(EnumType.STRING)
	private BienEnum type;
	private String ville;
	private String quartier;
	
	@ManyToOne
	@JoinColumn(name = "proprietaire_id", nullable = false)
	private Utilisateur proprietaire;
	
	@OneToMany(mappedBy = "bien", cascade = CascadeType.ALL)
	private List<Unite> unites = new ArrayList<Unite>();
	
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
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	public BienEnum getType() {
		return type;
	}
	public void setType(BienEnum type) {
		this.type = type;
	}
	public String getVille() {
		return ville;
	}
	public void setVille(String ville) {
		this.ville = ville;
	}

	public String getQuartier() {
		return quartier;
	}
	public void setQuartier(String quartier) {
		this.quartier = quartier;
	}
	public Utilisateur getProprietaire() {
		return proprietaire;
	}
	public void setProprietaire(Utilisateur proprietaire) {
		this.proprietaire = proprietaire;
	}
	public List<Unite> getUnites() {
		return unites;
	}
	public void setUnites(List<Unite> unites) {
		this.unites = unites;
	}
	
	
	
}
