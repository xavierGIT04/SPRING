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

import tg.ipnet.FirstSpring.DTO.Request.InscriptionDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.InscriptionDtoResponse;
import tg.ipnet.FirstSpring.service.InscriptionServiceImpl;

@RestController
@RequestMapping("/api/inscription/")
public class InscriptionController {
	@Autowired
	private InscriptionServiceImpl inscriptionServiceImpl;
	
	@PostMapping("/")
	public InscriptionDtoResponse create(@RequestBody InscriptionDtoRequest i) {
		return inscriptionServiceImpl.create(i);
	}

	@GetMapping("/")
	public List<InscriptionDtoResponse> list(){
		return inscriptionServiceImpl.list();
	}
	
	@PutMapping("/{id}/")
	public InscriptionDtoResponse updateInscription(@RequestBody InscriptionDtoRequest i, @PathVariable Long id) {
		return inscriptionServiceImpl.update(i, id);
	}
	
	@DeleteMapping("/{id}/")
	public void deleteEtudiant(@PathVariable Long id) {
		inscriptionServiceImpl.delete(id);
	}
	
	@GetMapping("/{id}/")
	public InscriptionDtoResponse getInscrit(@PathVariable Long id) {
		return inscriptionServiceImpl.getInscrit(id);
	}
}
