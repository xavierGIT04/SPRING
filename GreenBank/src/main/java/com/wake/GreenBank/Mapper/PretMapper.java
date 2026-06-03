package com.wake.GreenBank.Mapper;

import com.wake.GreenBank.dto.request.PretDtoRequest;
import com.wake.GreenBank.dto.response.PretDtoResponse;
import com.wake.GreenBank.entity.Pret;

public class PretMapper {
	
	public Pret dtoToPret(PretDtoRequest p) {
		Pret pret = new Pret();
		pret.setMontant(p.montant());
		pret.setDureeMois(p.dureeMois());
		pret.setStatut(p.statut());
		
		return pret;
	}
	
	public PretDtoResponse pretToDto(Pret p) {
		PretDtoResponse response = 
				new PretDtoResponse
				(
					p.getPublicId(),
					p.getMontant(),
					p.getDureeMois(),
					p.getStatut(),
					p.getClient().getNom()
					
				);
		return response;
	}

}
