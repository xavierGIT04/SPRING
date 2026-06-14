package com.ipnet.rentalapi.Gbails.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaiementResponse(
		UUID uuid,
	    LocalDateTime datePaiement,
	    BigDecimal montant,
	    String modePaiement,
	    String referencePaiement,
	    String recuUrl
	) {}