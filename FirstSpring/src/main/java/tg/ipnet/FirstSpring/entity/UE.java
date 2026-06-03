package tg.ipnet.FirstSpring.entity;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import tg.ipnet.FirstSpring.utils.BaseEntity;

@Entity
@Table(name="ue")
@EntityListeners(AuditingEntityListener.class)
public class UE extends BaseEntity{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long Id;
	
	@Column(nullable = false, unique = true)
	private String codeUe;
	
	@Column(nullable = false, length = 90)
	private String intitule;
	
	@Column(nullable = false, unique = true)
	private Integer creditECTS;
	
	@ManyToOne
	@JoinColumn(name = "type_ue_id", nullable = false)
	private TypeUE typeUe;
	
	@OneToMany(mappedBy = "ue")
	private List<Note> notes = new ArrayList<Note>();
	
	@ManyToMany(mappedBy = "ues")
	private Set<Programme> programmes = new HashSet<Programme>();

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getCodeUe() {
		return codeUe;
	}

	public void setCodeUe(String codeUe) {
		this.codeUe = codeUe;
	}

	public String getIntitule() {
		return intitule;
	}

	public void setIntitule(String intitule) {
		this.intitule = intitule;
	}

	public Integer getCreditECTS() {
		return creditECTS;
	}

	public void setCreditECTS(Integer creditECTS) {
		this.creditECTS = creditECTS;
	}

	public TypeUE getTypeUe() {
		return typeUe;
	}

	public void setTypeUe(TypeUE type) {
		this.typeUe = type;
	}

	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public Set<Programme> getProgrammes() {
		return programmes;
	}

	public void setProgrammes(Set<Programme> programmes) {
		this.programmes = programmes;
	}

}
