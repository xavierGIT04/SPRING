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

import tg.ipnet.FirstSpring.DTO.Request.AnneeScolaireDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.AnneeScolaireDtoResponse;
import tg.ipnet.FirstSpring.service.AnneeScolaireServiceImpl;

@RestController
@RequestMapping("/api/anneeScolaire/")
public class AnneeScolaireController {

	@Autowired
	public AnneeScolaireServiceImpl anneeScolaireServiceImpl;
	
	@PostMapping("/")
	public AnneeScolaireDtoResponse create(@RequestBody AnneeScolaireDtoRequest a) {
		return anneeScolaireServiceImpl.create(a);
	}

	@GetMapping("/")
	public List<AnneeScolaireDtoResponse> list(){
		return anneeScolaireServiceImpl.list();
	}
	
	@PutMapping("/{id}/")
	public AnneeScolaireDtoResponse updateEtudiant(@RequestBody AnneeScolaireDtoRequest a, @PathVariable Long id) {
		return anneeScolaireServiceImpl.update(a, id);
	}
	
	@DeleteMapping("/{id}/")
	public void deleteEtudiant(@PathVariable Long id) {
		anneeScolaireServiceImpl.delete(id);
	}
	
	@GetMapping("/{id}/")
	public AnneeScolaireDtoResponse getAnnee(@PathVariable Long id) {
		return anneeScolaireServiceImpl.getAnneeScolaire(id);
	}
}
