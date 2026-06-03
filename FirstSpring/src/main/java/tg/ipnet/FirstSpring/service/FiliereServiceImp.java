package tg.ipnet.FirstSpring.service;

import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Service;

import tg.ipnet.FirstSpring.DTO.Request.FiliereDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.FiliereDtoResponse;
import tg.ipnet.FirstSpring.Mappers.FiliereMappers;
import tg.ipnet.FirstSpring.entity.Filiere;
import tg.ipnet.FirstSpring.repository.FiliereRepository;

@Service
public class FiliereServiceImp implements FiliereService{

	private final FiliereRepository filiereRepository;
	
	public FiliereServiceImp(FiliereRepository filiereRepository) {
		this.filiereRepository = filiereRepository;
	}
	
	@Override
	public FiliereDtoResponse create(FiliereDtoRequest fdto) {
		FiliereMappers filiereMapper = new FiliereMappers();
		Filiere filiere = new Filiere();
		filiere =  filiereMapper.toFiliere(fdto);
		return filiereMapper.toDto(filiereRepository.save(filiere));

	}

	@Override
	public FiliereDtoResponse update(FiliereDtoRequest fdto, Long id) {
		Filiere filiere = filiereRepository.findById(id).orElseThrow(()-> new RuntimeException("Filière not found"));
		filiere.setNomFiliere(fdto.getNomFiliere());
		
		FiliereMappers filiereMapper = new FiliereMappers();

		return filiereMapper.toDto(filiereRepository.save(filiere));
	}

	@Override
	public void delete(Long id) {
		Filiere filiere = filiereRepository.findById(id).orElseThrow(()-> new RuntimeException("Filière not found"));
		filiereRepository.delete(filiere);
		
	}

	@Override
	public List<FiliereDtoResponse> list() {
		FiliereMappers filiereMapper = new FiliereMappers();
		
		List<Filiere> lesFilieres = filiereRepository.listFiliere();
		List<FiliereDtoResponse> filieresDto = new ArrayList<FiliereDtoResponse>();

		for (Filiere f : lesFilieres) {
		    filieresDto.add(filiereMapper.toDto(f));
		}
		return filieresDto;
	}

	@Override
	public FiliereDtoResponse getFiliere(Long id) {
		FiliereMappers filiereMapper = new FiliereMappers();
		
		Filiere filiere = filiereRepository.findById(id).orElseThrow(()-> new RuntimeException("Filière not found"));
		
		return filiereMapper.toDto(filiere);
	}

}
