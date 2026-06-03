package com.iitL32025.guniv.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Moto {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "code", nullable=false, length = 10)
	private String code;
	
	@Column(name = "marque", nullable=false)
	private String marque;
	
	@Column(name = "modele", nullable=false)
	private String modele;
	
	@Column(name = "couleur", nullable=false)
	private String couleur;
	
	@Column(name = "plaque_immatriculation", nullable=false)
	private String plaqueImmatriculation;
	
	@Column(name = "annee_sortie", nullable=false)
	private String anneeSortie;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMarque() {
		return marque;
	}

	public void setMarque(String marque) {
		this.marque = marque;
	}

	public String getModele() {
		return modele;
	}

	public void setModele(String modele) {
		this.modele = modele;
	}

	public String getCouleur() {
		return couleur;
	}

	public void setCouleur(String couleur) {
		this.couleur = couleur;
	}

	public String getPlaqueImmatriculation() {
		return plaqueImmatriculation;
	}

	public void setPlaqueImmatriculation(String plaqueImmatriculation) {
		this.plaqueImmatriculation = plaqueImmatriculation;
	}

	public String getAnneeSortie() {
		return anneeSortie;
	}

	public void setAnneeSortie(String anneeSortie) {
		this.anneeSortie = anneeSortie;
	}

	public Moto() {
		super();
		
	}
	
	
	
	
	
	
	
	

}
