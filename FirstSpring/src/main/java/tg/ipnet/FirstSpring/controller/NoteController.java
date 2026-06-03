package tg.ipnet.FirstSpring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tg.ipnet.FirstSpring.DTO.Request.NoteDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.NoteDtoResponse;
import tg.ipnet.FirstSpring.service.NoteServiceImp;

@RestController
@RequestMapping("/api/note/")
public class NoteController {
	@Autowired
	private NoteServiceImp noteServiceImp;
	
	@PostMapping("/")
	public NoteDtoResponse create(@RequestBody NoteDtoRequest n) {
		return noteServiceImp.create(n);
	}

	@GetMapping("/")
	public List<NoteDtoResponse> list(){
		return noteServiceImp.list();
	}
	
	@PutMapping("/{id}/")
	public NoteDtoResponse updateNote(@RequestBody NoteDtoRequest n, @PathVariable Long id) {
		return noteServiceImp.update(n, id);
	}
	
	@DeleteMapping("/{id}/")
	public void deleteEtudiant(@PathVariable Long id) {
		noteServiceImp.delete(id);
	}
	
	@GetMapping("/{id}/")
	public NoteDtoResponse getNote(@PathVariable Long id) {
		return noteServiceImp.getNote(id);
	}
}
