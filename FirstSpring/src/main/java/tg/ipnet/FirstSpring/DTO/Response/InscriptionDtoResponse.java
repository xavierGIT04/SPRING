package tg.ipnet.FirstSpring.DTO.Response;



import java.util.Date;


public class InscriptionDtoResponse {
	
	private Long Id;
	
	private String etatInscription;
	
	private Date dateInscription;
	
	private String parcours;
	
	private String filiere;
	
	private String  anneeScolaire;
	
	private String etudiant;
	
	private String matricule;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

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

	public String getParcours() {
		return parcours;
	}

	public void setParcours(String parcours) {
		this.parcours = parcours;
	}

	public String getAnneeScolaire() {
		return anneeScolaire;
	}

	public void setAnneeScolaire(String anneeScolaire) {
		this.anneeScolaire = anneeScolaire;
	}

	public String getEtudiant() {
		return etudiant;
	}

	public void setEtudiant(String etudiant) {
		this.etudiant = etudiant;
	}

	public String getMatricule() {
		return matricule;
	}

	public void setMatricule(String matricule) {
		this.matricule = matricule;
	}

	public String getFiliere() {
		return filiere;
	}

	public void setFiliere(String filiere) {
		this.filiere = filiere;
	}
	
	
	
	

}
