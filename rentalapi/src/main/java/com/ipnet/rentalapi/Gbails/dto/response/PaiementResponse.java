package com.ipnet.rentalapi.Gbails.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaiementResponse(
	    String uuid,
	    LocalDateTime datePaiement,
	    BigDecimal montant,
	    String modePaiement,
	    String referencePaiement,
	    String recuUrl
	) {}