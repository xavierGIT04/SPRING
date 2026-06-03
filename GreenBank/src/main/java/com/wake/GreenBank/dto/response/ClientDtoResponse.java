package com.wake.GreenBank.dto.response;

import java.util.UUID;


public record ClientDtoResponse(
		
		UUID publicId,String nom, String email, Float revenuAnnuel
		
		) {

}
