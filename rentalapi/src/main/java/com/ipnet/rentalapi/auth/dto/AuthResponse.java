package com.ipnet.rentalapi.auth.dto;

import java.util.UUID;

import com.ipnet.rentalapi.auth.ProfilEnum;
import com.ipnet.rentalapi.auth.RoleEnum;

public class AuthResponse {
	 private UUID uuid;
	 private String token;
	 private RoleEnum role;
	 private String nomComplet;
	 private ProfilEnum profil;
	 private String telephone;
	 
	 
	 public AuthResponse(UUID uuid, String token, String nomComplet, RoleEnum role, ProfilEnum profil, String telephone) {
		super();
		this.uuid = uuid;
		this.token = token;
		this.nomComplet = nomComplet;
		this.profil = profil;
		this.role = role;
		this.telephone = telephone;
		
		
	}
	 public String getToken() {
		 return token;
	 }
	 public void setToken(String token) {
		 this.token = token;
	 }
	 public RoleEnum getRole() {
		 return role;
	 }
	 public void setRole(RoleEnum role) {
		 this.role = role;
	 }
	 public String getNomComplet() {
		 return nomComplet;
	 }
	 public void setNomComplet(String nomComplet) {
		 this.nomComplet = nomComplet;
	 }
	 public UUID getUuid() {
		 return uuid;
	 }
	 public void setUuid(UUID uuid) {
		 this.uuid = uuid;
	 }
	 public ProfilEnum getProfil() {
		 return profil;
	 }
	 public void setProfil(ProfilEnum profil) {
		 this.profil = profil;
	 }
	 public String getTelephone() {
		 return telephone;
	 }
	 public void setTelephone(String telephone) {
		 this.telephone = telephone;
	 }
	 
	 
}
