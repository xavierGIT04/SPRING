package com.ipnet.rentalapi.auth.dto;

import com.ipnet.rentalapi.auth.ProfilEnum;
import com.ipnet.rentalapi.auth.RoleEnum;

public class UtilisateurRequest {

	private String telephone;
	private String codeProprietaire;
	private ProfilEnum profil;
	private RoleEnum role;
	private String nom;
	
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getCodeProprietaire() {
		return codeProprietaire;
	}
	public void setCodeProprietaire(String codeProprietaire) {
		this.codeProprietaire = codeProprietaire;
	}
	public ProfilEnum getProfil() {
		return profil;
	}
	public void setProfil(ProfilEnum profil) {
		this.profil = profil;
	}
	public RoleEnum getRole() {
		return role;
	}
	public void setRole(RoleEnum role) {
		this.role = role;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	
}
