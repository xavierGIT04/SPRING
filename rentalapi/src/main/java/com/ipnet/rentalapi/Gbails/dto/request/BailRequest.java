package com.ipnet.rentalapi.Gbails.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BailRequest(
    String locataireUuid,
    String uniteUuid,
    LocalDate dateDebut,
    LocalDate dateSortie,
    Integer duree,
    BigDecimal loyer,
    Integer jourEcheance,
    String conditions
) {}
