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

import tg.ipnet.FirstSpring.DTO.Request.TypeUeDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.TypeUeDtoResponse;
import tg.ipnet.FirstSpring.service.TypeUeServiceImpl;

@RestController
@RequestMapping("/api/typeUe/")
public class TypeUeController {
	
	@Autowired
	private TypeUeServiceImpl typeUeServiceImpl;
	
	@PostMapping("/")
	public TypeUeDtoResponse create(@RequestBody TypeUeDtoRequest t) {
		return typeUeServiceImpl.create(t);
	}

	@GetMapping("/")
	public List<TypeUeDtoResponse> list(){
		return typeUeServiceImpl.list();
	}
	
	@PutMapping("/{id}/")
	public TypeUeDtoResponse updateType(@RequestBody TypeUeDtoRequest t, @PathVariable Long id) {
		return typeUeServiceImpl.update(t, id);
	}
	
	@DeleteMapping("/{id}/")
	public void deleteEtudiant(@PathVariable Long id) {
		typeUeServiceImpl.delete(id);
	}
	
	@GetMapping("/{id}/")
	public TypeUeDtoResponse getType(@PathVariable Long id) {
		return typeUeServiceImpl.getType(id);
	}
}
