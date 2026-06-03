package tg.ipnet.FirstSpring.service;

import java.util.List;

import tg.ipnet.FirstSpring.DTO.Request.NoteDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.NoteDtoResponse;

public interface NoteService {
	
	public NoteDtoResponse create(NoteDtoRequest n);
	public NoteDtoResponse update(NoteDtoRequest n , Long id);
	public void delete(Long id);
	public List<NoteDtoResponse> list();
	public NoteDtoResponse getNote(Long id);
}
