package tg.ipnet.FirstSpring.Mappers;

import tg.ipnet.FirstSpring.DTO.Request.TypeUeDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.TypeUeDtoResponse;
import tg.ipnet.FirstSpring.entity.TypeUE;

public class TypeUeMappers {
	
	public TypeUeDtoResponse toTypeUeDto(TypeUE t) {
		
		TypeUeDtoResponse typeUeDto = new TypeUeDtoResponse();
		typeUeDto.setId(t.getId());
		typeUeDto.setLibelle(t.getLibelle());
		
		return typeUeDto;
	}
	
	public TypeUE toTypeUe(TypeUeDtoRequest t) {
		TypeUE typeUe = new TypeUE();
		typeUe.setLibelle(t.getLibelle());
		return typeUe;
	}
}
