package tg.ipnet.FirstSpring.DTO.Request;


public class ProgrammeDtoRequest {
	
	private String libelle;
	
	private String description;
	
	private Long parcours;
	
	private Long annee;

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

	public Long getParcours() {
		return parcours;
	}

	public void setParcours(Long parcours) {
		this.parcours = parcours;
	}

	public Long getAnnee() {
		return annee;
	}

	public void setAnnee(Long annee) {
		this.annee = annee;
	}
	
	
	

}
