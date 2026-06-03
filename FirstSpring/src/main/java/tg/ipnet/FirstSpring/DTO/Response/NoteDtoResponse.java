package tg.ipnet.FirstSpring.DTO.Response;

import java.util.Date;


public class NoteDtoResponse {
	
	private Long Id;
	
	private Double valeurNote;
	
	private Date dateObtention;
	
	private String typeEvaluation;
	
	private String ue;
	
	private String inscrit;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Double getValeurNote() {
		return valeurNote;
	}

	public void setValeurNote(Double valeurNote) {
		this.valeurNote = valeurNote;
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

	public String getUe() {
		return ue;
	}

	public void setUe(String ue) {
		this.ue = ue;
	}

	public String getInscrit() {
		return inscrit;
	}

	public void setInscrit(String inscrit) {
		this.inscrit = inscrit;
	}
	
	

}
