package tg.ipnet.FirstSpring.service;

import java.util.List;

import tg.ipnet.FirstSpring.DTO.Request.EtudiantDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.EtudiantDtoResponse;


public interface EtudiantService {
	public EtudiantDtoResponse create(EtudiantDtoRequest e);
	public EtudiantDtoResponse update(EtudiantDtoRequest e, Long id);
	public void delete(Long id);
	public List<EtudiantDtoResponse> list();
	public EtudiantDtoResponse getEtudiant(Long id);
}
