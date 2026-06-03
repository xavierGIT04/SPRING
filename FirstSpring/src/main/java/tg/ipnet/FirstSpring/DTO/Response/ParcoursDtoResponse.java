package tg.ipnet.FirstSpring.DTO.Response;


public class ParcoursDtoResponse {
	
	private Long id;
	
	private String libelle;
	
	private Long nbreSemestre;
	
	private String filiere;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getFiliere() {
		return filiere;
	}

	public void setFiliere(String filiere) {
		this.filiere = filiere;
	}
	
	
	

}
