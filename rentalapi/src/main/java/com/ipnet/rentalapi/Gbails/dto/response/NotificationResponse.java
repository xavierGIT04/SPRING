package com.ipnet.rentalapi.Gbails.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
		UUID uuid,
	    String titre,
	    String message,
	    LocalDateTime dateEnvoi,
	    String typeNotification,
	    boolean estLu
	) {}
