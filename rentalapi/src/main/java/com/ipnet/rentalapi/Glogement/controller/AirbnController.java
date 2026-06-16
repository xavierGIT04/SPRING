package com.ipnet.rentalapi.Glogement.controller;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.rentalapi.Glogement.Enums.StatutUnite;
import com.ipnet.rentalapi.Glogement.dto.request.AirbnRequest;
import com.ipnet.rentalapi.Glogement.dto.response.AirbnResponse;
import com.ipnet.rentalapi.Glogement.service.BienAirbnService;

@RestController
@RequestMapping("api/airbn/")
@PreAuthorize("hasRole('PROPRIETAIRE')")
public class AirbnController {

	@Autowired
	private BienAirbnService bienAirbnService;
	

	@PostMapping("add_airbn")
	public ResponseEntity<AirbnResponse> create(@RequestBody AirbnRequest request) {
		return ResponseEntity.ok(bienAirbnService.create(request));
	}
	
	
	@GetMapping("all_airbns")
	public ResponseEntity<ArrayList<AirbnResponse>> allAirbn(){
		return ResponseEntity.ok(bienAirbnService.allBien());
	}
	
	@GetMapping("one_airbn")
	public ResponseEntity<AirbnResponse> getOneAirbn(@RequestParam UUID id){
		return ResponseEntity.ok(bienAirbnService.getOneAirbn(id));
	}
	
	@PatchMapping("changer_statut")
	public ResponseEntity<AirbnResponse> change(UUID id, @RequestParam StatutUnite statut) {
		return ResponseEntity.ok(bienAirbnService.changerStatut(id, statut));
	}
	
	@PutMapping("update")
	public ResponseEntity<AirbnResponse> update(UUID id,@RequestBody AirbnRequest request) {
		return ResponseEntity.ok(bienAirbnService.update_infos(id, request));
	}
	
	
	
	
	
}
