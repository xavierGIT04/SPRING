package com.ipnet.rentalapi.Gbails.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EcheanceRequest(
		UUID contrat_id,
		LocalDate dateEcheance,
	    BigDecimal montantDu,
	    BigDecimal montantRestant,
	    String statut
		) {}
