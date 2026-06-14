package com.ipnet.rentalapi.Gbails.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.ipnet.rentalapi.Gbails.Enums.BailEnum;
import com.ipnet.rentalapi.Glogement.Enums.TypeUnite;

public  record BailResponse(
		UUID uuid,
	    String locataireNom,
	    String locataireTelephone,
	    String codeUnite,
	    TypeUnite typeUnite,
	    LocalDate dateDebut,
	    LocalDate dateSortie,
	    BigDecimal loyer,
	    BailEnum statut,
	    Integer jourEcheance,
	    Integer duree,
	    String condition
	   
	) {}
