package com.ipnet.rentalapi.Glogement.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.ipnet.rentalapi.Glogement.Enums.BienEnum;
import com.ipnet.rentalapi.Glogement.Enums.StatutUnite;
import com.ipnet.rentalapi.Glogement.Enums.TypeUnite;

public class AirbnResponse {

	private UUID uuidBien;
	private UUID uuidUnite;
	private String libelle;
	private BienEnum typebien = BienEnum.VILLA;
	private String ville;
	private String quartier;
	private String description;
	private TypeUnite typeUnite = TypeUnite.VILLA;
	private StatutUnite statut;
	private BigDecimal prix_nuite;
	public UUID getUuidBien() {
		return uuidBien;
	}
	public void setUuidBien(UUID uuidBien) {
		this.uuidBien = uuidBien;
	}
	public UUID getUuidUnite() {
		return uuidUnite;
	}
	public void setUuidUnite(UUID uuidUnite) {
		this.uuidUnite = uuidUnite;
	}
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	public BienEnum getTypebien() {
		return typebien;
	}
	public void setTypebien(BienEnum typebien) {
		this.typebien = typebien;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public TypeUnite getTypeUnite() {
		return typeUnite;
	}
	public void setTypeUnite(TypeUnite typeUnite) {
		this.typeUnite = typeUnite;
	}
	public StatutUnite getStatut() {
		return statut;
	}
	public void setStatut(StatutUnite statut) {
		this.statut = statut;
	}
	public BigDecimal getPrix_nuite() {
		return prix_nuite;
	}
	public void setPrix_nuite(BigDecimal prix_nuite) {
		this.prix_nuite = prix_nuite;
	}
	public AirbnResponse(UUID uuidBien, UUID uuidUnite, String libelle, BienEnum typebien, String ville,
			String quartier, String description, TypeUnite typeUnite, StatutUnite statut,
			BigDecimal prix_nuite) {
		super();
		this.uuidBien = uuidBien;
		this.uuidUnite = uuidUnite;
		this.libelle = libelle;
		this.typebien = typebien;
		this.ville = ville;
		this.quartier = quartier;
		this.description = description;
		this.typeUnite = typeUnite;
		this.statut = statut;
		this.prix_nuite = prix_nuite;
	}
	public AirbnResponse() {
		super();
		
	}
	
	
	
}
