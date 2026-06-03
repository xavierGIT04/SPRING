package tg.ipnet.FirstSpring.entity;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import tg.ipnet.FirstSpring.utils.BaseEntity;

@Entity
@Table(name="annee_scolaire")
@EntityListeners(AuditingEntityListener.class)
public class AnneeScolaire extends BaseEntity {

	@jakarta.persistence.Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long Id;
	
	@Column(nullable = false)
	private LocalDate dateDebut;
	
	@Column(nullable = false)
	private LocalDate dateFin;
	
	private Boolean estActive;
	
	@OneToMany(mappedBy = "annee")
	private List<Inscription> les_inscrits = new ArrayList<Inscription>();
	
	@OneToMany(mappedBy = "annee")
	private List<Programme> programmes = new ArrayList<Programme>();
	
	
	public List<Inscription> getLes_inscrits() {
		return les_inscrits;
	}
	public void setLes_inscrits(List<Inscription> les_inscrits) {
		this.les_inscrits = les_inscrits;
	}
	public List<Programme> getProgrammes() {
		return programmes;
	}
	public void setProgrammes(List<Programme> programmes) {
		this.programmes = programmes;
	}
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public LocalDate getDateDebut() {
		return dateDebut;
	}
	public void setDateDebut(LocalDate dateDebut) {
		this.dateDebut = dateDebut;
	}
	public LocalDate getDateFin() {
		return dateFin;
	}
	public void setDateFin(LocalDate dateFin) {
		this.dateFin = dateFin;
	}
	public Boolean getEstActive() {
		return estActive;
	}
	public void setEstActive(Boolean estActive) {
		this.estActive = estActive;
	}
	
	public AnneeScolaire() {
		super();
	}
	
	
}
