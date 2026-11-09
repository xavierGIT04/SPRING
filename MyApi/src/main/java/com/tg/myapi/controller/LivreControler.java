package com.tg.myapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tg.myapi.models.Livre;
import com.tg.myapi.service.LivreServiceImpl;

@RestController
@RequestMapping("livre/")
public class LivreControler {

	@Autowired
	private LivreServiceImpl service;
	
	@PostMapping("")
	public Livre add( @RequestBody Livre l){
		return service.add(l);
	}
	
	@GetMapping()
	public List<Livre> getLivres(){
		return service.getLivre();
	}
	
	
}
