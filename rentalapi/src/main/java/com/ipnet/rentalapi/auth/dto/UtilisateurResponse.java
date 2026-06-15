package com.ipnet.rentalapi.auth.dto;

import java.util.UUID;

import com.ipnet.rentalapi.auth.ProfilEnum;
import com.ipnet.rentalapi.auth.RoleEnum;

public class UtilisateurResponse {

	private UUID uuid;
	private String telephone;
	private ProfilEnum profil;
	private RoleEnum role;
	private String nom;
	private String avatar;
	
	public UtilisateurResponse(String telephone, ProfilEnum profil, RoleEnum role, String nom, String url, UUID id) {
		super();
		this.telephone = telephone;
		this.profil = profil;
		this.role = role;
		this.nom = nom;
		this.avatar = url;
		this.uuid = id;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
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
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
	
	
	
	
}
