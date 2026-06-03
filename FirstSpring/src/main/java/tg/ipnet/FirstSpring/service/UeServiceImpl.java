package tg.ipnet.FirstSpring.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import tg.ipnet.FirstSpring.DTO.Request.UeDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.UeDtoResponse;
import tg.ipnet.FirstSpring.Mappers.UeMappers;
import tg.ipnet.FirstSpring.entity.UE;
import tg.ipnet.FirstSpring.repository.TypeUeRepository;
import tg.ipnet.FirstSpring.repository.UeRepository;

@Service
public class UeServiceImpl implements UeService {

	private final UeRepository ueRepository;
	private final TypeUeRepository typeUeRepository;
	
	
	
	public UeServiceImpl(UeRepository ueRepository, TypeUeRepository typeUeRepository) {
		this.ueRepository = ueRepository;
		this.typeUeRepository = typeUeRepository;
	}

	@Override
	public UeDtoResponse create(UeDtoRequest u) {
		UE ue = new UE();
		UeMappers umap = new UeMappers(typeUeRepository);
		ue = umap.toUe(u);
		
		return umap.toUeDto(ueRepository.save(ue));
	}

	@Override
	public UeDtoResponse update(UeDtoRequest u, Long id) {
		UE ue = ueRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		

		UeMappers umap = new UeMappers(typeUeRepository);

		UE _ue = umap.toUe(u);
		ue.setCodeUe(_ue.getCodeUe());
		ue.setCreditECTS(_ue.getCreditECTS());
		ue.setIntitule(_ue.getIntitule());
		ue.setTypeUe(_ue.getTypeUe());

		return umap.toUeDto(ueRepository.save(ue));
	}

	@Override
	public void delete(Long id) {
		UE ue = ueRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		
		ueRepository.delete(ue);
	}

	@Override
	public List<UeDtoResponse> list() {
		UeMappers umap = new UeMappers(typeUeRepository);
		List<UE> lesUes = ueRepository.findAll();
		List<UeDtoResponse> lesUesDto = new ArrayList<UeDtoResponse>();

		for (UE u : lesUes) {
			lesUesDto.add(umap.toUeDto(u));
		}
		return lesUesDto;
	}

	@Override
	public UeDtoResponse getUe(Long id) {
		UE ue = ueRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		
		UeMappers umap = new UeMappers(typeUeRepository);

		return umap.toUeDto(ue);
	}

}
