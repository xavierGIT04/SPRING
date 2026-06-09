package com.ipnet.rentalapi.Glogement.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import com.ipnet.rentalapi.Glogement.Enums.StatutUnite;
import com.ipnet.rentalapi.Glogement.Enums.TypeUnite;

public class UniteRequest {
	
	private UUID bien_id;
	private String code_unite;
	private String description;
	private TypeUnite type;
	private StatutUnite statut;
	private BigDecimal loyer_reference;
	private BigDecimal prix_nuite;
	
	public UUID getBien_id() {
		return bien_id;
	}
	public void setBien_id(UUID bien_id) {
		this.bien_id = bien_id;
	}
	public String getCode_unite() {
		return code_unite;
	}
	public void setCode_unite(String code_unite) {
		this.code_unite = code_unite;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public TypeUnite getType() {
		return type;
	}
	public void setType(TypeUnite type) {
		this.type = type;
	}
	public StatutUnite getStatut() {
		return statut;
	}
	public void setStatut(StatutUnite statut) {
		this.statut = statut;
	}
	public BigDecimal getLoyer_reference() {
		return loyer_reference;
	}
	public void setLoyer_reference(BigDecimal loyer_reference) {
		this.loyer_reference = loyer_reference;
	}
	public BigDecimal getPrix_nuite() {
		return prix_nuite;
	}
	public void setPrix_nuite(BigDecimal prix_nuite) {
		this.prix_nuite = prix_nuite;
	}
	public UniteRequest(UUID bien_id, String code_unite, String description, TypeUnite type, StatutUnite statut,
			BigDecimal loyer_reference, BigDecimal prix_nuite) {
		super();
		this.bien_id = bien_id;
		this.code_unite = code_unite;
		this.description = description;
		this.type = type;
		this.statut = statut;
		this.loyer_reference = loyer_reference;
		this.prix_nuite = prix_nuite;
	}
	
	
	
}
