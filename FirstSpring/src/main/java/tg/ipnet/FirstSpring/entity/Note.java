package tg.ipnet.FirstSpring.entity;


import java.util.Date;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import tg.ipnet.FirstSpring.utils.BaseEntity;

@Entity
@Table(name="note")
@EntityListeners(AuditingEntityListener.class)
public class Note extends BaseEntity{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long Id;
	
	@Column(nullable = false)
	private Double valeurNote;
	
	@Column(nullable = false)
	private Date dateObtention;
	
	@Column(nullable = false, length = 60)
	private String typeEvaluation;
	
	@ManyToOne
	@JoinColumn(name = "ue_id", nullable = false)
	private UE ue;
	
	@ManyToOne
	@JoinColumn(name = "inscrit_id", nullable = false)
	private Inscription inscrit;
	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Double getValeuNote() {
		return valeurNote;
	}

	public void setValeuNote(Double valeuNote) {
		this.valeurNote = valeuNote;
	}

	public Date getDateObtention() {
		return dateObtention;
	}

	public void setDateObtention(Date dateObtention) {
		this.dateObtention = dateObtention;
	}

	public String getTypeEvaluation() {
		return typeEvaluation;
	}

	public void setTypeEvaluation(String typeEvaluation) {
		this.typeEvaluation = typeEvaluation;
	}

	public UE getUe() {
		return ue;
	}

	public void setUe(UE ue) {
		this.ue = ue;
	}

	public Inscription getInscrit() {
		return inscrit;
	}

	public void setInscrit(Inscription inscrit) {
		this.inscrit = inscrit;
	}

	
	
}
