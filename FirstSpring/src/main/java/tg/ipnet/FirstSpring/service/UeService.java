package tg.ipnet.FirstSpring.service;

import java.util.List;

import tg.ipnet.FirstSpring.DTO.Request.UeDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.UeDtoResponse;


public interface UeService {
	
	public UeDtoResponse create( UeDtoRequest u);
	public UeDtoResponse update(UeDtoRequest u, Long id);
	public void delete(Long id);
	public List<UeDtoResponse> list();
	public UeDtoResponse getUe(Long id);
}
