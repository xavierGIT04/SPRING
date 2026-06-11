package com.ipnet.rentalapi.auth.dto;

import com.ipnet.rentalapi.auth.ProfilEnum;
import com.ipnet.rentalapi.auth.RoleEnum;

public class UtilisateurResponse {

	private String telephone;
	private ProfilEnum profil;
	private RoleEnum role;
	private String nom;
	public UtilisateurResponse(String telephone, ProfilEnum profil, RoleEnum role, String nom) {
		super();
		this.telephone = telephone;
		this.profil = profil;
		this.role = role;
		this.nom = nom;
	}
	
	
}
