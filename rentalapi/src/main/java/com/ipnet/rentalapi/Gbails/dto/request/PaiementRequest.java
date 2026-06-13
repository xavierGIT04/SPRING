package com.ipnet.rentalapi.Gbails.dto.request;

import java.math.BigDecimal;

public record PaiementRequest(
	    String echeanceUuid,
	    BigDecimal montant,
	    String modePaiement,
	    String referencePaiement
	) {}
