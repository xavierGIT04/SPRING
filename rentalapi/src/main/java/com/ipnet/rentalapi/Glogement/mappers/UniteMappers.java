package com.ipnet.rentalapi.Glogement.mappers;

import org.springframework.stereotype.Component;

import com.ipnet.rentalapi.Glogement.dto.request.UniteRequest;
import com.ipnet.rentalapi.Glogement.dto.response.UniteResponse;
import com.ipnet.rentalapi.Glogement.models.Unite;

@Component
public class UniteMappers {

	public Unite toEntity(UniteRequest unite) {
		Unite response = new Unite();
		response.setCode_unite(unite.getCode_unite());
		response.setDescription(unite.getDescription());
		response.setLoyer_reference(unite.getLoyer_reference());
		response.setPrix_nuite(unite.getPrix_nuite());
		response.setStatut(unite.getStatut());
		response.setType(unite.getType());
		return response;
	}
	
	public UniteResponse toResponse(Unite unite) {
		return new UniteResponse(
				unite.getUuid(), 
				unite.getCode_unite(), 
				unite.getDescription(), 
				unite.getType(), 
				unite.getStatut(), 
				unite.getLoyer_reference(), 
				unite.getPrix_nuite(),
				unite.getBien().getType()+"-"+unite.getBien().getLibelle()
				);
	}
}
