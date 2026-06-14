package com.ipnet.rentalapi.Gbails.dto.request;

import java.util.UUID;

/**
 * Requête d'initiation d'un paiement Mobile Money (Flooz / TMoney) via FedaPay.
 *
 * @param echeanceUuid UUID de l'échéance à payer
 * @param telephone    numéro Mobile Money du locataire au format international (ex: +22891000000)
 */
public record InitPaiementMobileRequest(
        UUID echeanceUuid,
        String telephone
) {}