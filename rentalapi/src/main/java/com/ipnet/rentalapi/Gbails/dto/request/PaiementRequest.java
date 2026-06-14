package com.ipnet.rentalapi.Gbails.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import com.ipnet.rentalapi.Gbails.Enums.ModePaiement;

public record PaiementRequest(
		UUID echeanceUuid,
	    BigDecimal montant,
	    ModePaiement modePaiement,
	    String referencePaiement
	) {}
