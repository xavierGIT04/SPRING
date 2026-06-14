package com.ipnet.rentalapi.Gbails.dto.request;

import com.ipnet.rentalapi.Gbails.Enums.TypeNotification;
import com.ipnet.rentalapi.auth.model.Utilisateur;

public record NotificationRequest(
		Utilisateur destinataire,
		String titre,
	    String message,
	    TypeNotification typeNotification
		
		) {}
