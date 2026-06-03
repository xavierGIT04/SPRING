package tg.ipnet.FirstSpring.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import tg.ipnet.FirstSpring.DTO.Request.ProgrammeDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.ProgrammeDtoResponse;
import tg.ipnet.FirstSpring.Mappers.ProgrammeMappers;
import tg.ipnet.FirstSpring.entity.Programme;
import tg.ipnet.FirstSpring.repository.AnneeScolaireRepository;
import tg.ipnet.FirstSpring.repository.ParcoursRepository;
import tg.ipnet.FirstSpring.repository.ProgrammeRepository;

@Service
public class ProgrammeServiceImpl implements ProgrammeService {

	private final ProgrammeRepository programmeRepository;
	private final ParcoursRepository parcoursRepository;
	private final AnneeScolaireRepository anneeScolaireRepository;
	
	
	public ProgrammeServiceImpl(ProgrammeRepository programmeRepository, ParcoursRepository parcoursRepository,
			AnneeScolaireRepository anneeScolaireRepository) {
		
		this.programmeRepository = programmeRepository;
		this.parcoursRepository = parcoursRepository;
		this.anneeScolaireRepository = anneeScolaireRepository;
	}

	@Override
	public ProgrammeDtoResponse create(ProgrammeDtoRequest p) {
		Programme programme = new Programme();
		ProgrammeMappers pmap = new ProgrammeMappers(parcoursRepository, anneeScolaireRepository);
		programme = pmap.toProgramme(p);
		
		return pmap.toProgrammeDto(programmeRepository.save(programme));
	}

	@Override
	public ProgrammeDtoResponse update(ProgrammeDtoRequest p, Long id) {
		Programme programme = programmeRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		

		ProgrammeMappers pmap = new ProgrammeMappers(parcoursRepository, anneeScolaireRepository);

		Programme _programme = pmap.toProgramme(p);
		programme.setAnnee(_programme.getAnnee());
		programme.setDescription(_programme.getDescription());
		programme.setLibelle(_programme.getLibelle());
		programme.setParcours(_programme.getParcours());

		return pmap.toProgrammeDto(programmeRepository.save(programme));
	}

	@Override
	public void delete(Long id) {
		Programme programme = programmeRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		
		programmeRepository.delete(programme);
	}

	@Override
	public List<ProgrammeDtoResponse> list() {
		ProgrammeMappers pmap = new ProgrammeMappers(parcoursRepository, anneeScolaireRepository);
		List<Programme> lesProgrammes = programmeRepository.findAll();
		List<ProgrammeDtoResponse> lesProgrammesDto = new ArrayList<ProgrammeDtoResponse>();

		for (Programme p : lesProgrammes) {
			lesProgrammesDto.add(pmap.toProgrammeDto(p));
		}
		return lesProgrammesDto;
	}

	@Override
	public ProgrammeDtoResponse getProgramme(Long id) {
		Programme programme = programmeRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		
		ProgrammeMappers pmap = new ProgrammeMappers(parcoursRepository, anneeScolaireRepository);

		return pmap.toProgrammeDto(programme);
	}

}
