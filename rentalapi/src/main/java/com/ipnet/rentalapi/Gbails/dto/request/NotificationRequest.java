package com.ipnet.rentalapi.Gbails.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationRequest(
		UUID destinataire_id,
		String titre,
	    String message,
	    LocalDateTime dateEnvoi,
	    String typeNotification,
	    boolean estLu
		
		) {}
