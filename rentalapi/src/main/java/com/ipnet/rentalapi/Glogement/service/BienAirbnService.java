package com.ipnet.rentalapi.Glogement.service;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipnet.rentalapi.Glogement.Enums.StatutUnite;
import com.ipnet.rentalapi.Glogement.dto.request.AirbnRequest;
import com.ipnet.rentalapi.Glogement.dto.response.AirbnResponse;
import com.ipnet.rentalapi.Glogement.models.Bien;
import com.ipnet.rentalapi.Glogement.models.Unite;
import com.ipnet.rentalapi.Glogement.repository.BienRepository;
import com.ipnet.rentalapi.Glogement.repository.UniteRepository;
import com.ipnet.rentalapi.auth.service.AuthUtils;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BienAirbnService{

	private final BienRepository bienRepository;
	private final UniteRepository uniteRepository;
	@Autowired
	private AuthUtils authUtils;
	
	public BienAirbnService(BienRepository bienRepository, UniteRepository uniteRepository) {
		super();
		this.bienRepository = bienRepository;
		this.uniteRepository = uniteRepository;
	}
	
	public AirbnResponse create(AirbnRequest request) {
		Bien bien = new Bien();
		bien.setLibelle(request.getLibelle());
		bien.setQuartier(request.getQuartier());
		bien.setVille(request.getVille());
		bien.setType(request.getTypebien());
		bien.setProprietaire(authUtils.getUtilisateurConnecte());
		Bien bienDb = bienRepository.save(bien);
		
		Unite unite = new Unite();
		unite.setCode_unite(null);
		unite.setDescription(request.getDescription());
		unite.setLoyer_reference(null);
		unite.setPrix_nuite(request.getPrix_nuite());
		unite.setStatut(request.getStatut());
		unite.setType(request.getTypeUnite());
		
		unite.setBien(bienDb);
		Unite uniteDb = uniteRepository.save(unite);
		
		return new AirbnResponse(
				bienDb.getUuid(), uniteDb.getUuid(), 
				bienDb.getLibelle(), bienDb.getType(), 
				bien.getVille(), bienDb.getQuartier(), 
				uniteDb.getDescription(), 
				uniteDb.getType(), uniteDb.getStatut(), 
				uniteDb.getPrix_nuite()
				);	
		
	}
	
	public AirbnResponse getOneAirbn(UUID id)  {
		Bien bien;
		AirbnResponse response = new AirbnResponse();
		try {
			bien = bienRepository.findByUuid(id).orElseThrow(() -> new RuntimeException("Bien introuvable"));
			Unite unite = bien.getUnites().get(0);
			response = new AirbnResponse(
					bien.getUuid(), unite.getUuid(), 
					bien.getLibelle(), bien.getType(), 
					bien.getVille(), bien.getQuartier(), 
					unite.getDescription(), 
					unite.getType(), unite.getStatut(), 
					unite.getPrix_nuite()
				);	
  		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return response;
		
	}

	public ArrayList<AirbnResponse> allBien(){
		List<Bien> biens = bienRepository.findBiensByProprietaireTelephone(authUtils.getTelephone());
		ArrayList<AirbnResponse> aibns = new ArrayList<AirbnResponse>() ;
		
		
		
		for (Bien bien : biens) {
			if (bien.getUnites() == null || bien.getUnites().isEmpty()) {
		        throw new RuntimeException("Cet Airbn n'existe pas");
		    }
			Unite unite = bien.getUnites().get(0); 
			aibns.add(
					new AirbnResponse(
							bien.getUuid(), unite.getUuid(), 
							bien.getLibelle(), bien.getType(), 
							bien.getVille(), bien.getQuartier(), 
							unite.getDescription(), 
							unite.getType(), unite.getStatut(), 
							unite.getPrix_nuite()
					)	
			);
		}
		
		return aibns;
	}
	
	public AirbnResponse changerStatut(UUID id, StatutUnite statut) {
		Bien bien;
		AirbnResponse response = new AirbnResponse();
		try {
			bien = bienRepository.findByUuid(id).orElseThrow(() -> new RuntimeException("Bien introuvable"));
			
			if (bien.getUnites() == null || bien.getUnites().isEmpty()) {
		        throw new RuntimeException("Cet Airbn n'existe pas");
		    }
			
			Unite unite = bien.getUnites().get(0);
			unite.setStatut(statut);
			
			response = new AirbnResponse(
					bien.getUuid(), unite.getUuid(), 
					bien.getLibelle(), bien.getType(), 
					bien.getVille(), bien.getQuartier(), 
					unite.getDescription(), 
					unite.getType(), unite.getStatut(), 
					unite.getPrix_nuite()
				);	
  		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		return response;
		
		
	}
	
	public AirbnResponse update_infos(UUID id ,AirbnRequest request) {
		Bien bien;
		AirbnResponse response = new AirbnResponse();
		try {
			bien = bienRepository.findByUuid(id).orElseThrow(() -> new RuntimeException("Bien introuvable"));
			
			bien.setLibelle(request.getLibelle());
			bien.setQuartier(request.getQuartier());
			bien.setVille(request.getVille());
			bien.setType(request.getTypebien());
			bien.setProprietaire(authUtils.getUtilisateurConnecte());
			
			if (bien.getUnites() == null || bien.getUnites().isEmpty()) {
		        throw new RuntimeException("Cet Airbn n'existe pas");
		    }
			
			Unite unite = bien.getUnites().get(0);
		
			unite.setCode_unite(null);
			unite.setDescription(request.getDescription());
			unite.setLoyer_reference(null);
			unite.setPrix_nuite(request.getPrix_nuite());
			unite.setStatut(request.getStatut());
			unite.setType(request.getTypeUnite());
			
			response = new AirbnResponse(
					bien.getUuid(), unite.getUuid(), 
					bien.getLibelle(), bien.getType(), 
					bien.getVille(), bien.getQuartier(), 
					unite.getDescription(), 
					unite.getType(), unite.getStatut(), 
					unite.getPrix_nuite()
				);	
  		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		return response;
	}

}
