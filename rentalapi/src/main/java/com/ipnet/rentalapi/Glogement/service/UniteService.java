package com.ipnet.rentalapi.Glogement.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipnet.rentalapi.Glogement.Enums.StatutUnite;
import com.ipnet.rentalapi.Glogement.dto.request.UniteRequest;
import com.ipnet.rentalapi.Glogement.dto.response.UniteResponse;
import com.ipnet.rentalapi.Glogement.mappers.UniteMappers;
import com.ipnet.rentalapi.Glogement.models.Bien;
import com.ipnet.rentalapi.Glogement.models.Unite;
import com.ipnet.rentalapi.Glogement.repository.BienRepository;
import com.ipnet.rentalapi.Glogement.repository.UniteRepository;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class UniteService{

	private final UniteRepository uniteRepository;
	private final BienRepository bienRepository;
	@Autowired
	private UniteMappers mappers;
	
	public UniteService(UniteRepository uniteRepository, BienRepository bienRepository) {
		super();
		this.uniteRepository = uniteRepository;
		this.bienRepository = bienRepository;
	}
 
	
	public UniteResponse create(UniteRequest request) {
		Unite unite = mappers.toEntity(request);
		try {
			Bien bien;
			bien = bienRepository.findByUuid(request.getBien_id()).orElseThrow(() -> new RuntimeException("Bien introuvable"));
			unite.setBien(bien);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		return mappers.toResponse(uniteRepository.save(unite));
	}

	
	public UniteResponse getOneUnite(UUID id) throws Exception {
		Unite unite = uniteRepository.findByUuid(id).orElseThrow(()-> new RuntimeException("Unité introuvable"));
		return mappers.toResponse(unite);
	}

	
	public UniteResponse changeStatut(UUID id, StatutUnite statut) {
		Unite unite = null;
		try {
			unite = uniteRepository.findByUuid(id).orElseThrow(()-> new Exception("Unité introuvable"));
			unite.setStatut(statut);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mappers.toResponse(unite);
	}

	public UniteResponse update_infos(UUID id, UniteRequest request) {
		Unite unite = null;
		try {
			unite = uniteRepository.findByUuid(id).orElseThrow(()-> new Exception("Unité introuvable"));
			unite = mappers.toEntity(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mappers.toResponse(unite);
	}
}
