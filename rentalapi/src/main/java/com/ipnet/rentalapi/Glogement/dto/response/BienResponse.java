package com.ipnet.rentalapi.Glogement.dto.response;

import java.util.UUID;

import com.ipnet.rentalapi.Glogement.Enums.BienEnum;

public class BienResponse {
	
	private UUID uuid;
	private String proprietaire_name;
	private String libelle;
	private BienEnum type;
	private String ville;
	private String quartier;
	
	

	public BienResponse(UUID uuid, String libelle, BienEnum type, String ville,
			String quartier, String name) {
		super();
		this.uuid = uuid;
		this.libelle = libelle;
		this.type = type;
		this.ville = ville;
		this.quartier = quartier;
		this.proprietaire_name = name;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getProprietaire_name() {
		return proprietaire_name;
	}

	public void setProprietaire_name(String proprietaire_name) {
		this.proprietaire_name = proprietaire_name;
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
	
	
	
	

}
