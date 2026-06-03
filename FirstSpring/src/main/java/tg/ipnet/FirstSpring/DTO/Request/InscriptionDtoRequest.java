package tg.ipnet.FirstSpring.DTO.Request;

import java.util.Date;


public class InscriptionDtoRequest {
	
	private String etatInscription;
	
	private Date dateInscription;
	
	private Long parcours;
	
	private Long  anneeScolaire;
	
	private Long etudiant;

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

	public Long getParcours() {
		return parcours;
	}

	public void setParcours(Long parcours) {
		this.parcours = parcours;
	}

	public Long getAnneeScolaire() {
		return anneeScolaire;
	}

	public void setAnneeScolaire(Long anneeScolaire) {
		this.anneeScolaire = anneeScolaire;
	}

	public Long getEtudiant() {
		return etudiant;
	}

	public void setEtudiant(Long etudiant) {
		this.etudiant = etudiant;
	}

	
	
	
	
}
