package tg.ipnet.FirstSpring.entity;


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
@Table(name="filiere")
@EntityListeners(AuditingEntityListener.class)
public class Filiere extends BaseEntity  {
	
	@jakarta.persistence.Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long Id;
	
	@Column(nullable = false, unique = true)
	private String nomFiliere;

	@OneToMany(mappedBy = "filiere")
	private List<Parcours> les_parcours = new ArrayList<Parcours>();
	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getNomFiliere() {
		return nomFiliere;
	}

	public void setNomFiliere(String nomFiliere) {
		this.nomFiliere = nomFiliere;
	}

	public List<Parcours> getLes_parcours() {
		return les_parcours;
	}

	public void setLes_parcours(List<Parcours> les_parcours) {
		this.les_parcours = les_parcours;
	}
	
	


}
