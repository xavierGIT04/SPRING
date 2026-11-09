package com.tg.myapi.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "livre")
public class Livre {
	
	@jakarta.persistence.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	
	@Column(nullable=false)
	private String titre;
	
	@Column(nullable=false)
	private String auteur;
	
	@Column(nullable=false, length=10)
	private String isbn;
	
	@Column(nullable=false)
	private int nombreExemplaire;
	
	@Column(nullable=false)
	private int nombreExemplaireDispo;
	
	
	public Long getId() {
		return this.Id;
	}
	
	public String getTitre() {
		return this.titre;
	}
	
	public String getAuteur() {
		return this.auteur;
	}
	
	public String getIsbn() {
		return this.isbn;
	}
	
	public int getNombrExemplaire() {
		return this.nombreExemplaire;
	}
	
	public int getNombrExemplaireDispo() {
		return this.nombreExemplaireDispo;
	}
	
	public void setTitre(String t) {
		this.titre=t;
	}
	
	public void setAuteur(String a) {
		this.auteur=a;
	}
	
	public void setIsbn(String i) {
		this.isbn=i;
	}
	
	public void setNombreExemplaire(int n) {
		this.nombreExemplaire=n;
	}
	
	public void setNombreExemplaireDispo(int n) {
		this.nombreExemplaireDispo=n;
	}
	
	public Livre() {
		
	}

}
