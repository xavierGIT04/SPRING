package tg.ipnet.FirstSpring.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import tg.ipnet.FirstSpring.utils.BaseEntity;

@Entity
@Table(name="parcours")
@EntityListeners(AuditingEntityListener.class)
public class Parcours extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="libelle", nullable=false, unique=false, length=101)
	private String libelle;
	
	@Column(name = "nbre_semestre")
	private Long nbreSemestre;
	
	@ManyToOne
	@JoinColumn(name = "filiere_id", nullable = false)
	private Filiere filiere;
	
	@OneToMany(mappedBy = "parcours")
	private List<Inscription> les_inscrits = new ArrayList<Inscription>();

	@OneToMany(mappedBy = "parcours")
	private List<Programme> programmes = new ArrayList<Programme>();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public Long getNbreSemestre() {
		return nbreSemestre;
	}

	public void setNbreSemestre(Long nbreSemestre) {
		this.nbreSemestre = nbreSemestre;
	}

	public Filiere getFiliere() {
		return filiere;
	}

	public void setFiliere(Filiere filiere) {
		this.filiere = filiere;
	}

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

	

}
