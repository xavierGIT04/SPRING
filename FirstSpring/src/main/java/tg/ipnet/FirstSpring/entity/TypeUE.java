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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import tg.ipnet.FirstSpring.utils.BaseEntity;

@Entity
@Table(name="type_ue")
@EntityListeners(AuditingEntityListener.class/* c'est la classe qui va réagir aux évènement @CreatedDate .... et les remplir automatiquement*/)//faire l'écoute de la classe
public class TypeUE extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="libelle", nullable=false, unique=true, length=101)
	private String libelle;
	
	@OneToMany(mappedBy = "typeUe")
	private List<UE> les_ues = new ArrayList<UE>();
	
	public Long getId() {
		return id;
	}

	public List<UE> getLes_ues() {
		return les_ues;
	}

	public void setLes_ues(List<UE> les_ues) {
		this.les_ues = les_ues;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLibelle() {
		return libelle;
	}

	@Override
	public String toString() {
		return "TypeUE [id=" + id + ", libelle=" + libelle + "]";
	}

	public TypeUE() {
		super();
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	
	

}
