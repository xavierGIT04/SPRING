package com.ipnet.rentalapi.Gbails.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EcheanceResponse(
	    String uuid,
	    LocalDate dateEcheance,
	    BigDecimal montantDu,
	    BigDecimal montantRestant,
	    String statut,
	    String codeUnite
	) {}
