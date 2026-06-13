package com.ipnet.rentalapi.Gbails.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public  record BailResponse(
	    String uuid,
	    String locataireNom,
	    String locataireTelephone,
	    String codeUnite,
	    String typeUnite,
	    LocalDate dateDebut,
	    LocalDate dateSortie,
	    BigDecimal loyer,
	    String statut,
	    Integer jourEcheance
	) {}
