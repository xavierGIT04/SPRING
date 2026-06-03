package tg.ipnet.FirstSpring.Mappers;

import tg.ipnet.FirstSpring.DTO.Request.AnneeScolaireDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.AnneeScolaireDtoResponse;
import tg.ipnet.FirstSpring.entity.AnneeScolaire;

public class AnneeScolaireMappers {
	
	public AnneeScolaireDtoResponse toAnneeScolaireDto(AnneeScolaire a) {
		AnneeScolaireDtoResponse anneeDto = new AnneeScolaireDtoResponse();
		anneeDto.setDateDebut(a.getDateDebut());
		anneeDto.setDateFin(a.getDateFin());
		anneeDto.setEstActive(a.getEstActive());
		anneeDto.setId(a.getId());
		return anneeDto;
	}
	
	public AnneeScolaire toAnneeScolaire(AnneeScolaireDtoRequest a) {
		AnneeScolaire annee = new AnneeScolaire();
		annee.setDateDebut(a.getDateDebut());
		annee.setDateFin(a.getDateFin());
		annee.setEstActive(a.getEstActive());
		return annee;
	}

}
