package tg.ipnet.FirstSpring.Mappers;

import tg.ipnet.FirstSpring.DTO.Request.ParcoursDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.ParcoursDtoResponse;
import tg.ipnet.FirstSpring.entity.Filiere;
import tg.ipnet.FirstSpring.entity.Parcours;
import tg.ipnet.FirstSpring.repository.FiliereRepository;

public class ParcoursMappers {
	private FiliereRepository filiereRepository;
	
	public ParcoursMappers(FiliereRepository filiereRepository) {
		this.filiereRepository = filiereRepository;
	}
	
	public ParcoursDtoResponse toParcoursDto(Parcours p) {
		
		ParcoursDtoResponse parcoursDto = new ParcoursDtoResponse();
		parcoursDto.setFiliere(p.getFiliere().getNomFiliere());
		parcoursDto.setId(p.getId());
		parcoursDto.setLibelle(p.getLibelle());
		parcoursDto.setNbreSemestre(p.getNbreSemestre());
		return parcoursDto;
	}
	
	public Parcours toParcours(ParcoursDtoRequest p) {
		Parcours parcours = new Parcours();
		Filiere filiere = filiereRepository.findById(p.getFiliere()).orElseThrow(()-> new RuntimeException("Filière not found"));

		parcours.setFiliere(filiere);
		parcours.setLibelle(p.getLibelle());
		parcours.setNbreSemestre(p.getNbreSemestre());
		return parcours;
	}

}
