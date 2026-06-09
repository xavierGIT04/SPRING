package com.ipnet.rentalapi.Glogement.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.ipnet.rentalapi.Glogement.Enums.StatutUnite;
import com.ipnet.rentalapi.Glogement.Enums.TypeUnite;

public class UniteResponse {
	
	private UUID uuid;
	private String bien_name;
	private String code_unite;
	private String description;
	private TypeUnite type;
	private StatutUnite statut;
	private BigDecimal loyer_reference;
	private BigDecimal prix_nuite;
	public UniteResponse(UUID uuid, String code_unite,String description, TypeUnite type,
			StatutUnite statut, BigDecimal loyer_reference, BigDecimal prix_nuite, String bien_name) {
		super();
		this.uuid = uuid;
		this.code_unite = code_unite;
		this.description = description;
		this.type = type;
		this.statut = statut;
		this.loyer_reference = loyer_reference;
		this.prix_nuite = prix_nuite;
		this.bien_name = bien_name;
	}
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public String getBien_name() {
		return bien_name;
	}
	public void setBien_name(String bien_name) {
		this.bien_name = bien_name;
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
	
	

}
