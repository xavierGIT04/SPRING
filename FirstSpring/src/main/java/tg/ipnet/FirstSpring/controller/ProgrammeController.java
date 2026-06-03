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

import tg.ipnet.FirstSpring.DTO.Request.ProgrammeDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.ProgrammeDtoResponse;
import tg.ipnet.FirstSpring.service.ProgrammeServiceImpl;

@RestController
@RequestMapping("/api/programme/")
public class ProgrammeController {
	
	@Autowired
	private ProgrammeServiceImpl programmeServiceImpl;
	
	@PostMapping("/")
	public ProgrammeDtoResponse create(@RequestBody ProgrammeDtoRequest p) {
		return programmeServiceImpl.create(p);
	}

	@GetMapping("/")
	public List<ProgrammeDtoResponse> list(){
		return programmeServiceImpl.list();
	}
	
	@PutMapping("/{id}/")
	public ProgrammeDtoResponse updateProgramme(@RequestBody ProgrammeDtoRequest p, @PathVariable Long id) {
		return programmeServiceImpl.update(p, id);
	}
	
	@DeleteMapping("/{id}/")
	public void deleteEtudiant(@PathVariable Long id) {
		programmeServiceImpl.delete(id);
	}
	
	@GetMapping("/{id}/")
	public ProgrammeDtoResponse getProgramme(@PathVariable Long id) {
		return programmeServiceImpl.getProgramme(id);
	}
}
