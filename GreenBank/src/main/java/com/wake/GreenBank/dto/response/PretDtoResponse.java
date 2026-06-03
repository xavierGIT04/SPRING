package com.wake.GreenBank.dto.response;

import java.util.UUID;

import com.wake.GreenBank.enums.Statut;



public record PretDtoResponse(
		UUID publicId, Float montant, int dureeMois, Statut statut , String client
		
		) {

}
