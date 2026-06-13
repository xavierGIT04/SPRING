package com.ipnet.rentalapi.Gbails.dto.response;

import java.time.LocalDateTime;

public record NotificationResponse(
	    String uuid,
	    String titre,
	    String message,
	    LocalDateTime dateEnvoi,
	    String typeNotification,
	    boolean estLu
	) {}
