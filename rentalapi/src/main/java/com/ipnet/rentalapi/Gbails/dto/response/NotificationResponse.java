package com.ipnet.rentalapi.Gbails.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ipnet.rentalapi.Gbails.Enums.TypeNotification;

public record NotificationResponse(
		UUID uuid,
	    String titre,
	    String message,
	    LocalDateTime dateEnvoi,
	    TypeNotification typeNotification,
	    boolean estLu
	) {}
