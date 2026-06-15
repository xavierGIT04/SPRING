package com.ipnet.rentalapi.Gbails.dto.request;

import java.time.LocalDate;
import java.util.UUID;


public record EcheanceRequest(
		UUID contrat_id,
		LocalDate dateEcheance
		) {}
