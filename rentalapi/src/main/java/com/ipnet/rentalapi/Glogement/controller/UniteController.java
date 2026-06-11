package com.ipnet.rentalapi.Glogement.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.rentalapi.Glogement.Enums.StatutUnite;
import com.ipnet.rentalapi.Glogement.dto.request.UniteRequest;
import com.ipnet.rentalapi.Glogement.dto.response.UniteResponse;
import com.ipnet.rentalapi.Glogement.service.UniteService;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/unite")
@PreAuthorize("hasRole('PROPRIETAIRE')")
@CrossOrigin("http://localhost:4200/")
public class UniteController {
	
	@Autowired
	private  UniteService uniteService;

	@PostMapping("/")
	public ResponseEntity<UniteResponse> create(@RequestBody UniteRequest request) {
		return ResponseEntity.ok(uniteService.create(request));
	}
	
	@PatchMapping("/changer_statut")
	public ResponseEntity<UniteResponse> change(UUID id, @RequestParam StatutUnite statut) {
		return ResponseEntity.ok(uniteService.changeStatut(id, statut));
	}
	

}
