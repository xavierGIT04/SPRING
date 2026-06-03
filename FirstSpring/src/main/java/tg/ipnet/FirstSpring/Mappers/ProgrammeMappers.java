package tg.ipnet.FirstSpring.Mappers;

import tg.ipnet.FirstSpring.DTO.Request.ProgrammeDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.ProgrammeDtoResponse;
import tg.ipnet.FirstSpring.entity.AnneeScolaire;
import tg.ipnet.FirstSpring.entity.Parcours;
import tg.ipnet.FirstSpring.entity.Programme;
import tg.ipnet.FirstSpring.repository.AnneeScolaireRepository;
import tg.ipnet.FirstSpring.repository.ParcoursRepository;

public class ProgrammeMappers {
	
	private ParcoursRepository parcoursRepository;
	private AnneeScolaireRepository anneeScolaireRepository;
	
	public ProgrammeMappers(ParcoursRepository parcoursRepository, AnneeScolaireRepository anneeScolaireRepository) {
		this.anneeScolaireRepository = anneeScolaireRepository;
		this.parcoursRepository = parcoursRepository;
	}
	
	public ProgrammeDtoResponse toProgrammeDto(Programme p) {
		
		ProgrammeDtoResponse programmeDto = new ProgrammeDtoResponse();
		programmeDto.setId(p.getId());
		programmeDto.setAnnee(p.getAnnee().getDateDebut().getYear()+"-"+p.getAnnee().getDateFin().getYear());
		programmeDto.setDescription(p.getDescription());
		programmeDto.setLibelle(p.getLibelle());
		programmeDto.setParcours(p.getParcours().getLibelle());
		
		return programmeDto;
	}
	
	public Programme toProgramme(ProgrammeDtoRequest p) {
		Programme programme = new Programme();
		AnneeScolaire annee = anneeScolaireRepository.findById(p.getAnnee()).orElseThrow(()-> new RuntimeException("not found"));
		Parcours parcours = parcoursRepository.findById(p.getParcours()).orElseThrow(()-> new RuntimeException("not found"));
		
		programme.setAnnee(annee);
		programme.setDescription(p.getDescription());
		programme.setLibelle(p.getLibelle());
		programme.setParcours(parcours);
		return programme;
	}


}
