package tg.ipnet.FirstSpring.service;

import java.util.List;

import tg.ipnet.FirstSpring.DTO.Request.ParcoursDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.ParcoursDtoResponse;



public interface ParcoursService {
	
	public ParcoursDtoResponse create(ParcoursDtoRequest p);
	public ParcoursDtoResponse update(ParcoursDtoRequest p, Long id);
	public void delete(Long id);
	public List<ParcoursDtoResponse> list();
	public ParcoursDtoResponse getParcours(Long id);
}
