package com.iitL32025.guniv.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iitL32025.guniv.models.Moto;
import com.iitL32025.guniv.service.MotoService;

@RestController
@RequestMapping("api/moto/")
@CrossOrigin(origins = "*")
public class MotoController {
	
	@Autowired
	private MotoService ms;
	
	@PostMapping("/save")
	public Moto save(@RequestBody Moto m) {
		return ms.add(m);
	}
	
	@GetMapping("/")
	public List<Moto> all(){
		return ms.getMotos();
	}
	

}
