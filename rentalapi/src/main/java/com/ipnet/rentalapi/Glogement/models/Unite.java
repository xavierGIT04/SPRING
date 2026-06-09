package com.ipnet.rentalapi.Glogement.models;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ipnet.rentalapi.Glogement.Enums.StatutUnite;
import com.ipnet.rentalapi.Glogement.Enums.TypeUnite;
import com.ipnet.rentalapi.utils.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Unite extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private UUID uuid = UUID.randomUUID();
	
	@Column(unique = true, nullable = true)
	private String code_unite;
	private String description;
	private TypeUnite type;
	private StatutUnite statut;
	
	@Column(nullable = true)
	private BigDecimal loyer_reference;
	
	@Column(nullable = true)
	private BigDecimal prix_nuite;

	@ManyToOne
	@JoinColumn(name = "bien_id", nullable = false)
	private Bien bien;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
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

	public Bien getBien() {
		return bien;
	}

	public void setBien(Bien bien) {
		this.bien = bien;
	}

	
	
	
	
}
