package com.ipnet.rentalapi.Glogement.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipnet.rentalapi.Glogement.dto.request.BienRequest;
import com.ipnet.rentalapi.Glogement.dto.response.BienResponse;
import com.ipnet.rentalapi.Glogement.dto.response.UniteResponse;
import com.ipnet.rentalapi.Glogement.mappers.BienMappers;
import com.ipnet.rentalapi.Glogement.mappers.UniteMappers;
import com.ipnet.rentalapi.Glogement.models.Bien;
import com.ipnet.rentalapi.Glogement.models.Unite;
import com.ipnet.rentalapi.Glogement.repository.BienRepository;
import com.ipnet.rentalapi.auth.service.AuthUtils;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BienService{
	private final BienRepository bienRepository;
	@Autowired
	private BienMappers mappers;
	
	@Autowired
	private UniteMappers umappers;
	
	@Autowired
	private AuthUtils authUtils;
	
	public BienService(BienRepository bienRepository) {
		super();
		
		this.bienRepository = bienRepository;
	}
	
	public BienResponse create(BienRequest bien) {
		Bien bienDb = bienRepository.save(mappers.toEntity(bien));
		return mappers.toResponse(bienDb);
	}
	
	public BienResponse getOneBien(UUID id) throws Exception {
		Bien bien = bienRepository.findByUuid(id).orElseThrow(() -> new RuntimeException("Bien introuvable"));
		return mappers.toResponse(bien);
	}
	
	public ArrayList<BienResponse> allBien() {
		List<Bien> biens = bienRepository.findBiensByProprietaireTelephone(authUtils.getTelephone());
		ArrayList<BienResponse> response = new ArrayList<BienResponse>();
		for (Bien bien : biens) {
			response.add(mappers.toResponse(bien));
		}
		return response;
	}
	
	public ArrayList<UniteResponse> getAllUniteByBien(UUID id)  {
		Bien bien;
		ArrayList<UniteResponse> response = new ArrayList<UniteResponse>();
		try {
			bien = bienRepository.findByUuid(id).orElseThrow(() -> new RuntimeException("Bien introuvable"));
			
			if (bien.getUnites() == null || bien.getUnites().isEmpty()) {
		        throw new RuntimeException("Ce bien ne possède aucune unité");
		    }
			List<Unite> unites = bien.getUnites();	
			for (Unite unite : unites) {
				response.add(umappers.toResponse(unite));
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
		
	}
	
	public BienResponse update_infos(UUID id , BienRequest request) {
		Bien bien=null;
		
		try {
			bien = bienRepository.findByUuid(id).orElseThrow(() -> new RuntimeException("Bien introuvable"));
			bien = mappers.toEntity(request);	
				
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return mappers.toResponse(bien);
		
	}
	
	
}
