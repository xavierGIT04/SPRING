package com.ipnet.rentalapi.Glogement.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ipnet.rentalapi.Glogement.dto.request.BienRequest;
import com.ipnet.rentalapi.Glogement.dto.response.BienResponse;
import com.ipnet.rentalapi.Glogement.models.Bien;
import com.ipnet.rentalapi.auth.service.AuthUtils;

@Component
public class BienMappers {
	
	@Autowired
	private  AuthUtils authUtil;
	
	public Bien toEntity(BienRequest request) {
		Bien bien = new Bien();
		bien.setLibelle(request.getLibelle());
		bien.setQuartier(request.getQuartier());
		bien.setVille(request.getVille());
		bien.setType(request.getType());
		bien.setProprietaire(authUtil.getUtilisateurConnecte());
		return bien;
	}
	
	public BienResponse toResponse(Bien bien) {
		return new BienResponse(
				bien.getUuid(), 
				bien.getLibelle(), 
				bien.getType(), 
				bien.getQuartier(), 
				bien.getVille(),
				bien.getProprietaire().getNomComplet()
			);
	}

}
