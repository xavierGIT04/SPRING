package tg.ipnet.FirstSpring.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import tg.ipnet.FirstSpring.DTO.Request.TypeUeDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.TypeUeDtoResponse;
import tg.ipnet.FirstSpring.Mappers.TypeUeMappers;
import tg.ipnet.FirstSpring.entity.TypeUE;
import tg.ipnet.FirstSpring.repository.TypeUeRepository;

@Service
public  class TypeUeServiceImpl implements TypeUeService {

	private final TypeUeRepository typeUeRepository;

	
	
	public TypeUeServiceImpl(TypeUeRepository typeUeRepository) {

		this.typeUeRepository = typeUeRepository;
	}

	@Override
	public TypeUeDtoResponse create(TypeUeDtoRequest t) {
		TypeUE typeUe = new TypeUE();
		TypeUeMappers tmap = new TypeUeMappers();
		typeUe = tmap.toTypeUe(t);
		
		return tmap.toTypeUeDto(typeUeRepository.save(typeUe));
	}

	@Override
	public TypeUeDtoResponse update(TypeUeDtoRequest t, Long id) {
		TypeUE typeUe = typeUeRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		

		TypeUeMappers tmap = new TypeUeMappers();

		TypeUE _type = tmap.toTypeUe(t);
		typeUe.setLibelle(_type.getLibelle());
		

		return tmap.toTypeUeDto(typeUeRepository.save(typeUe));
	}

	@Override
	public void delete(Long id) {
		TypeUE typeUe = typeUeRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		
		typeUeRepository.delete(typeUe);

	}

	@Override
	public List<TypeUeDtoResponse> list() {
		TypeUeMappers tmap = new TypeUeMappers();
		List<TypeUE> lesTypeUes = typeUeRepository.findAll();
		List<TypeUeDtoResponse> lesTypeUesDto = new ArrayList<TypeUeDtoResponse>();

		for (TypeUE t : lesTypeUes) {
			lesTypeUesDto.add(tmap.toTypeUeDto(t));
		}
		return lesTypeUesDto;
	}

	@Override
	public TypeUeDtoResponse getType(Long id) {
		TypeUE typeUe = typeUeRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		
		TypeUeMappers tmap = new TypeUeMappers();

		return tmap.toTypeUeDto(typeUe);
	}

}
