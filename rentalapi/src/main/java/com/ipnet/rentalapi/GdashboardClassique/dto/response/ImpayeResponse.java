package com.ipnet.rentalapi.GdashboardClassique.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.ipnet.rentalapi.Gbails.Enums.StatutEcheance;

public record ImpayeResponse(
		UUID contratUuid,
		UUID echeanceUuid,
		String locataireNom,
		String locataireTelephone,
		String codeUnite,
		LocalDate dateEcheance,
		BigDecimal montantRestant,
		StatutEcheance statut,
		long joursDeRetard
) {}
