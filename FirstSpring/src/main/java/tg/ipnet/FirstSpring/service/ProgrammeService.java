package tg.ipnet.FirstSpring.service;

import java.util.List;

import tg.ipnet.FirstSpring.DTO.Request.ProgrammeDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.ProgrammeDtoResponse;



public interface ProgrammeService {
	
	public ProgrammeDtoResponse create(ProgrammeDtoRequest p);
	public ProgrammeDtoResponse update(ProgrammeDtoRequest p, Long id);
	public void delete(Long id);
	public List<ProgrammeDtoResponse> list();
	public ProgrammeDtoResponse getProgramme(Long id);
}
