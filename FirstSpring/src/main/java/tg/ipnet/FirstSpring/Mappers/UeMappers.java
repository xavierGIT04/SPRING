package tg.ipnet.FirstSpring.Mappers;

import tg.ipnet.FirstSpring.DTO.Request.UeDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.UeDtoResponse;
import tg.ipnet.FirstSpring.entity.TypeUE;
import tg.ipnet.FirstSpring.entity.UE;
import tg.ipnet.FirstSpring.repository.TypeUeRepository;

public class UeMappers {
	private TypeUeRepository typeUeRepository;

	public UeMappers(TypeUeRepository typeUeRepository) {
		this.typeUeRepository = typeUeRepository;
	}
	
	public UeDtoResponse toUeDto(UE e) {
		
		UeDtoResponse ueDto = new UeDtoResponse();
		ueDto.setId(e.getId());
		ueDto.setIntitule(e.getIntitule());
		ueDto.setCodeUe(e.getCodeUe());
		ueDto.setCreditECTS(e.getCreditECTS());
		ueDto.setTypeUe(e.getTypeUe().getLibelle());

		return ueDto;
	}
	
	public UE toUe(UeDtoRequest e) {
		UE ue = new UE();
		TypeUE typeUe = typeUeRepository.findById(e.getTypeUe()).orElseThrow(()-> new RuntimeException("not found"));

		ue.setIntitule(e.getIntitule());
		ue.setCodeUe(e.getCodeUe());
		ue.setCreditECTS(e.getCreditECTS());
		ue.setTypeUe(typeUe);
		return ue;
	}
}
