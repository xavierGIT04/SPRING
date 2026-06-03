package tg.ipnet.FirstSpring.DTO.Request;


public class ParcoursDtoRequest {
	
	private String libelle;
	
	private Long nbreSemestre;
	
	private Long filiere;

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public Long getNbreSemestre() {
		return nbreSemestre;
	}

	public void setNbreSemestre(Long nbreSemestre) {
		this.nbreSemestre = nbreSemestre;
	}

	public Long getFiliere() {
		return filiere;
	}

	public void setFiliere(Long filiere) {
		this.filiere = filiere;
	}
	
	

}
