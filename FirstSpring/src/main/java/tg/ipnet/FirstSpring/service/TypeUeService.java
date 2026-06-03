package tg.ipnet.FirstSpring.service;

import java.util.List;

import tg.ipnet.FirstSpring.DTO.Request.TypeUeDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.TypeUeDtoResponse;



public interface TypeUeService {
	
	public TypeUeDtoResponse create( TypeUeDtoRequest t);
	public TypeUeDtoResponse update(TypeUeDtoRequest t, Long id);
	public void delete(Long id);
	public List<TypeUeDtoResponse> list();
	public TypeUeDtoResponse getType(Long id);
}
