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

import tg.ipnet.FirstSpring.DTO.Request.ParcoursDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.ParcoursDtoResponse;
import tg.ipnet.FirstSpring.service.ParcoursServiceImpl;

@RestController
@RequestMapping("/api/parcours/")
public class ParcoursController {
	@Autowired
	private ParcoursServiceImpl parcoursServiceImpl;
	
	@PostMapping("/")
	public ParcoursDtoResponse create(@RequestBody ParcoursDtoRequest p) {
		return parcoursServiceImpl.create(p);
	}

	@GetMapping("/")
	public List<ParcoursDtoResponse> list(){
		return parcoursServiceImpl.list();
	}
	
	@PutMapping("/{id}/")
	public ParcoursDtoResponse updateParcours(@RequestBody ParcoursDtoRequest p, @PathVariable Long id) {
		return parcoursServiceImpl.update(p, id);
	}
	
	@DeleteMapping("/{id}/")
	public void deleteEtudiant(@PathVariable Long id) {
		parcoursServiceImpl.delete(id);
	}
	
	@GetMapping("/{id}/")
	public ParcoursDtoResponse getParcours(@PathVariable Long id) {
		return parcoursServiceImpl.getParcours(id);
	}
}
