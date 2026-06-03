package tg.ipnet.FirstSpring.entity;



import java.util.HashSet;
import java.util.Set;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import tg.ipnet.FirstSpring.utils.BaseEntity;

@Entity
@Table(name="programme")
@EntityListeners(AuditingEntityListener.class)
public class Programme extends BaseEntity{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long Id;
	
	@Column(nullable = false, length = 60)
	private String libelle;
	
	@Column(nullable = false, length = 100)
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "parcours_id", nullable = false)
	private Parcours parcours;
	
	@ManyToOne
	@JoinColumn(name = "annee_scolaire_id", nullable = false)
	private AnneeScolaire annee;
	
	@ManyToMany
	@JoinTable(
		name = "Programme_Ue",
		joinColumns = @JoinColumn(name = "programme_id"),
		inverseJoinColumns = @JoinColumn(name= "ue_id")
	)
	private Set<UE> ues = new HashSet<UE>();
	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

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

	public Set<UE> getUes() {
		return ues;
	}

	public void setUes(Set<UE> ues) {
		this.ues = ues;
	}

	
}
