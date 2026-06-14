package com.ipnet.rentalapi.Gbails.dto.response;

import java.math.BigDecimal;

public record PaiementInitResult(String paymentUrl, BigDecimal montant) {}
