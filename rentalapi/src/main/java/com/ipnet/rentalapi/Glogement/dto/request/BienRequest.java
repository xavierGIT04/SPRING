package com.ipnet.rentalapi.Glogement.dto.request;


import com.ipnet.rentalapi.Glogement.Enums.BienEnum;

public class BienRequest {
	
	private String libelle;
	private BienEnum type;
	private String ville;
	private String quartier;
	
	
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
	public BienRequest(String libelle, BienEnum type, String ville, String quartier) {
		super();
		this.libelle = libelle;
		this.type = type;
		this.ville = ville;
		this.quartier = quartier;
	}
	
	
	
	
}
