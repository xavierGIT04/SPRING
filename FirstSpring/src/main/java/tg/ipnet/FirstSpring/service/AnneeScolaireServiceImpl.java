package tg.ipnet.FirstSpring.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import tg.ipnet.FirstSpring.DTO.Request.AnneeScolaireDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.AnneeScolaireDtoResponse;
import tg.ipnet.FirstSpring.Mappers.AnneeScolaireMappers;
import tg.ipnet.FirstSpring.entity.AnneeScolaire;
import tg.ipnet.FirstSpring.repository.AnneeScolaireRepository;

@Service
public class AnneeScolaireServiceImpl implements AnneeScolaireService {

	
	private final AnneeScolaireRepository anneeScolaireRepository;
	
	public AnneeScolaireServiceImpl(AnneeScolaireRepository anneeScolaireRepository) {
		this.anneeScolaireRepository = anneeScolaireRepository;
	}
	
	@Override
	public AnneeScolaireDtoResponse create(AnneeScolaireDtoRequest a) {
		AnneeScolaire annee = new AnneeScolaire();
		AnneeScolaireMappers anneeMapper = new AnneeScolaireMappers();
		annee = anneeMapper.toAnneeScolaire(a);
		
		return anneeMapper.toAnneeScolaireDto(anneeScolaireRepository.save(annee));
	}

	@Override
	public AnneeScolaireDtoResponse update(AnneeScolaireDtoRequest a, Long id) {
		AnneeScolaire annee = anneeScolaireRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));
		annee.setDateDebut(a.getDateDebut());
		annee.setDateFin(a.getDateFin());
		annee.setEstActive(a.getEstActive());
		AnneeScolaireMappers anneeMapper = new AnneeScolaireMappers();
		return anneeMapper.toAnneeScolaireDto(anneeScolaireRepository.save(annee));
	}

	@Override
	public void delete(Long id) {
		AnneeScolaire annee = anneeScolaireRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));
		anneeScolaireRepository.delete(annee);
	}

	@Override
	public List<AnneeScolaireDtoResponse> list() {
		List<AnneeScolaire> annees = anneeScolaireRepository.listByDateRecent();
		List<AnneeScolaireDtoResponse> anneesDto = new ArrayList<>();
		AnneeScolaireMappers anneeMapper = new AnneeScolaireMappers();

		for (AnneeScolaire anneeScolaire : annees) {
		   anneesDto.add(anneeMapper.toAnneeScolaireDto(anneeScolaire));
		}
		return anneesDto;
	}

	@Override
	public AnneeScolaireDtoResponse getAnneeScolaire(Long id) {
		AnneeScolaire annee = anneeScolaireRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));
		AnneeScolaireMappers anneeMapper = new AnneeScolaireMappers();
		
		return anneeMapper.toAnneeScolaireDto(annee);
	}



}
