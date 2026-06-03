package tg.ipnet.FirstSpring.service;

import java.util.List;

import tg.ipnet.FirstSpring.DTO.Request.AnneeScolaireDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.AnneeScolaireDtoResponse;


public interface AnneeScolaireService {
	
	public AnneeScolaireDtoResponse create(AnneeScolaireDtoRequest a);
	public AnneeScolaireDtoResponse update(AnneeScolaireDtoRequest a, Long id);
	public void delete(Long id);
	public List<AnneeScolaireDtoResponse> list();
	public AnneeScolaireDtoResponse getAnneeScolaire(Long id);
	
}
