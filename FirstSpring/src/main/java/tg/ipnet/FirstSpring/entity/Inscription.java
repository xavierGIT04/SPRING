package tg.ipnet.FirstSpring.entity;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import tg.ipnet.FirstSpring.utils.BaseEntity;

@Entity
@Table(name = "inscrit")
@EntityListeners(AuditingEntityListener.class)

public class Inscription extends BaseEntity{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long Id;
	
	@Column(nullable = false, length = 60)
	private String etatInscription;
	
	@Column(nullable = false)
	private Date dateInscription;
	
	@ManyToOne
	@JoinColumn(name = "parcours_id", nullable = false)
	private Parcours parcours;
	 
	@ManyToOne
	@JoinColumn(name = "annee_scolaire_id", nullable = false)
	private AnneeScolaire annee;
	
	@ManyToOne
	@JoinColumn(name = "etudiant_id", nullable = false)
	private Etudiant etudiant;
	
	@OneToMany(mappedBy = "inscrit")
	private List<Note> notes = new ArrayList<Note>();
	
	
	
	
	public Parcours getParcours() {
		return parcours;
	}


	public void setParcours(Parcours parcours) {
		this.parcours = parcours;
	}


	public AnneeScolaire getAnnee() {
		return annee;
	}


	public void setAnnee(AnneeScolaire annee) {
		this.annee = annee;
	}


	public Etudiant getEtudiant() {
		return etudiant;
	}


	public void setEtudiant(Etudiant etudiant) {
		this.etudiant = etudiant;
	}


	public List<Note> getNotes() {
		return notes;
	}


	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}


	public Inscription() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Long getId() {
		return Id;
	}


	public void setId(Long id) {
		Id = id;
	}


	public String getEtatInscription() {
		return etatInscription;
	}


	public void setEtatInscription(String etatInscription) {
		this.etatInscription = etatInscription;
	}


	public Date getDateInscription() {
		return dateInscription;
	}


	public void setDateInscription(Date dateInscription) {
		this.dateInscription = dateInscription;
	}


	
}
