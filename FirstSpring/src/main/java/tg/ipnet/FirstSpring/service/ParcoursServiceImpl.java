package tg.ipnet.FirstSpring.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import tg.ipnet.FirstSpring.DTO.Request.ParcoursDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.ParcoursDtoResponse;
import tg.ipnet.FirstSpring.Mappers.ParcoursMappers;
import tg.ipnet.FirstSpring.entity.Parcours;
import tg.ipnet.FirstSpring.repository.FiliereRepository;
import tg.ipnet.FirstSpring.repository.ParcoursRepository;

@Service
public class ParcoursServiceImpl implements ParcoursService {

	private final ParcoursRepository parcoursRepository;
	private final FiliereRepository filiereRepository;
	
	
	
	public ParcoursServiceImpl(ParcoursRepository parcoursRepository, FiliereRepository filiereRepository) {
		this.parcoursRepository = parcoursRepository;
		this.filiereRepository = filiereRepository;
	}

	@Override
	public ParcoursDtoResponse create(ParcoursDtoRequest p) {
		Parcours parcours = new Parcours();
		ParcoursMappers pmap = new ParcoursMappers(filiereRepository);
		
		parcours = pmap.toParcours(p);
		
		return pmap.toParcoursDto(parcoursRepository.save(parcours));
	}

	@Override
	public ParcoursDtoResponse update(ParcoursDtoRequest p, Long id) {
		Parcours parcours = parcoursRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		

		ParcoursMappers pmap = new ParcoursMappers(filiereRepository);

		Parcours _parcours = pmap.toParcours(p);
		parcours.setFiliere(_parcours.getFiliere());
		parcours.setLibelle(_parcours.getLibelle());
		parcours.setNbreSemestre(_parcours.getNbreSemestre());

		return pmap.toParcoursDto(parcoursRepository.save(parcours));
	}

	@Override
	public void delete(Long id) {
		Parcours parcours = parcoursRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		
		parcoursRepository.delete(parcours);
		
	}

	@Override
	public List<ParcoursDtoResponse> list() {
		ParcoursMappers pmap = new ParcoursMappers(filiereRepository);
		List<Parcours> lesParcours = parcoursRepository.findAll();
		List<ParcoursDtoResponse> lesParcoursDto = new ArrayList<ParcoursDtoResponse>();

		for (Parcours p : lesParcours) {
			lesParcoursDto.add(pmap.toParcoursDto(p));
		}
		return lesParcoursDto;
	}

	@Override
	public ParcoursDtoResponse getParcours(Long id) {
		Parcours parcours = parcoursRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		
		ParcoursMappers pmap = new ParcoursMappers(filiereRepository);

		return pmap.toParcoursDto(parcours);
	}

}
