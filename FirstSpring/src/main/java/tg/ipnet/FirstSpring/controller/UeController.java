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

import tg.ipnet.FirstSpring.DTO.Request.UeDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.UeDtoResponse;
import tg.ipnet.FirstSpring.service.UeServiceImpl;

@RestController
@RequestMapping("/api/ue/")
public class UeController {
	
	@Autowired
	private UeServiceImpl ueServiceImpl;
	
	@PostMapping("/")
	public UeDtoResponse create(@RequestBody UeDtoRequest u) {
		return ueServiceImpl.create(u);
	}

	@GetMapping("/")
	public List<UeDtoResponse> list(){
		return ueServiceImpl.list();
	}
	
	@PutMapping("/{id}/")
	public UeDtoResponse updateUe(@RequestBody UeDtoRequest u, @PathVariable Long id) {
		return ueServiceImpl.update(u, id);
	}
	
	@DeleteMapping("/{id}/")
	public void deleteEtudiant(@PathVariable Long id) {
		ueServiceImpl.delete(id);
	}
	
	@GetMapping("/{id}/")
	public UeDtoResponse getEtudiant(@PathVariable Long id) {
		return ueServiceImpl.getUe(id);
	}

}
