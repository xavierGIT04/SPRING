package tg.ipnet.FirstSpring.DTO.Response;

import java.time.LocalDate;


public class AnneeScolaireDtoResponse {
	private Long Id;
	
	private LocalDate dateDebut;
	
	private LocalDate dateFin;
	
	private Boolean estActive;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public LocalDate getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(LocalDate dateDebut) {
		this.dateDebut = dateDebut;
	}

	public LocalDate getDateFin() {
		return dateFin;
	}

	public void setDateFin(LocalDate dateFin) {
		this.dateFin = dateFin;
	}

	public Boolean getEstActive() {
		return estActive;
	}

	public void setEstActive(Boolean estActive) {
		this.estActive = estActive;
	}
	
	

}
