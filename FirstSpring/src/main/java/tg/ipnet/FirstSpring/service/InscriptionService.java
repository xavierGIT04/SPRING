package tg.ipnet.FirstSpring.service;

import java.util.List;

import tg.ipnet.FirstSpring.DTO.Request.InscriptionDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.InscriptionDtoResponse;

public interface InscriptionService {
	
	public InscriptionDtoResponse create(InscriptionDtoRequest i);
	public InscriptionDtoResponse update(InscriptionDtoRequest i, Long id);
	public void delete(Long id);
	public List<InscriptionDtoResponse> list();
	public InscriptionDtoResponse getInscrit(Long id);

}
