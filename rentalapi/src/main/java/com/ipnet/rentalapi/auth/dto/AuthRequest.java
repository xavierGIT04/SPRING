package com.ipnet.rentalapi.auth.dto;

public class AuthRequest {

	private String telephone;
    private String codeProprietaire;
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
    
    
}
