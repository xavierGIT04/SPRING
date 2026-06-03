package com.iitL32025.guniv.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iitL32025.guniv.models.Etudiant;
import com.iitL32025.guniv.service.EtudiantService;

@RestController
@RequestMapping("api/etudiant/")
@CrossOrigin(origins = "*")
public class EtudiantController {

	@Autowired
	private EtudiantService etudiantService;
	
	@PostMapping("/")
	public Etudiant save(@RequestBody Etudiant e) {
		return etudiantService.add(e);
	}
	
	@GetMapping("/")
	public List<Etudiant> all(){
		return etudiantService.getEtudiants();
	}
	
	@GetMapping("/{id}/")
	public Etudiant one(@PathVariable Long id) {
		return etudiantService.getEtudiant(id);
	}
	
	@DeleteMapping("/{id}/")
	public void suppprimer(@PathVariable Long id) {
		etudiantService.sup(id);
	}
	
	@PutMapping("/{id}/")
	public void upd(@RequestBody Etudiant e, @PathVariable Long id) {
		etudiantService.putEtudiant(e,id);
	}
}
