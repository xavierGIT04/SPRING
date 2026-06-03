package tg.ipnet.FirstSpring.DTO.Request;

import java.util.Date;


public class NoteDtoRequest {
	
	private Double valeurNote;
	
	private Date dateObtention;
	
	private String typeEvaluation;
	
	private Long ue;
	
	private Long inscrit;

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

	public Long getUe() {
		return ue;
	}

	public void setUe(Long ue) {
		this.ue = ue;
	}

	public Long getInscrit() {
		return inscrit;
	}

	public void setInscrit(Long inscrit) {
		this.inscrit = inscrit;
	}

	
	

}
