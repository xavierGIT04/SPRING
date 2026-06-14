package com.ipnet.rentalapi.Gbails.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BailRequest(
	UUID locataireUuid,
	UUID uniteUuid,
    LocalDate dateDebut,
    LocalDate dateSortie,
    BigDecimal loyer,
    Integer jourEcheance,
    String conditions
) {}
