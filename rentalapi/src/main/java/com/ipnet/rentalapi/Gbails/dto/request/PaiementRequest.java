package com.ipnet.rentalapi.Gbails.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record PaiementRequest(
		UUID echeanceUuid,
	    BigDecimal montant,
	    String modePaiement,
	    String referencePaiement
	) {}
