package tg.ipnet.FirstSpring.service;

import java.util.List;

import tg.ipnet.FirstSpring.DTO.Request.FiliereDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.FiliereDtoResponse;

public interface FiliereService {
	
	public FiliereDtoResponse create(FiliereDtoRequest fdto);
	public FiliereDtoResponse update(FiliereDtoRequest fdto, Long id);
	public void delete(Long id);
	public List<FiliereDtoResponse> list();
	public FiliereDtoResponse getFiliere(Long id);
}
