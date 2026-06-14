package com.ipnet.rentalapi.Gbails.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.ipnet.rentalapi.Gbails.Enums.StatutEcheance;

public record EcheanceResponse(
		UUID uuid,
	    LocalDate dateEcheance,
	    BigDecimal montantDu,
	    BigDecimal montantRestant,
	    StatutEcheance statut,
	    String codeUnite
	) {}
