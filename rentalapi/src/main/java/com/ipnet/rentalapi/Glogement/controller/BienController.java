package com.ipnet.rentalapi.Glogement.controller;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.rentalapi.Glogement.dto.request.BienRequest;
import com.ipnet.rentalapi.Glogement.dto.response.BienResponse;
import com.ipnet.rentalapi.Glogement.dto.response.UniteResponse;
import com.ipnet.rentalapi.Glogement.service.BienService;

import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/bien/")
@PreAuthorize("hasRole('PROPRIETAIRE')")
public class BienController {

	
	@Autowired
	private BienService bienService;
	
	@PostMapping("add")
	public ResponseEntity<BienResponse> create(@RequestBody BienRequest request) {
		return ResponseEntity.ok(bienService.create(request));
	}
	
	@GetMapping("all")
	public ResponseEntity<ArrayList<BienResponse>> allBien(){
		return ResponseEntity.ok(bienService.allBien());
	}
	
	@PutMapping("update")
	public ResponseEntity<BienResponse> update(@RequestParam UUID id, @RequestBody BienRequest request) throws Exception{
		return ResponseEntity.ok(bienService.update(id, request));
	}
	
	
	
	@GetMapping("get_unites")
	public ResponseEntity<ArrayList<UniteResponse>> allUniteByBien(@RequestParam UUID id){
		return ResponseEntity.ok(bienService.getAllUniteByBien(id));
	}
	
	
}
