package com.ipnet.rentalapi.Gbails.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Réponse à une demande d'initiation de paiement Mobile Money.
 *
 * @param echeanceUuid UUID de l'échéance concernée
 * @param montant      montant en FCFA
 * @param paymentUrl   URL de la page de paiement FedaPay (à ouvrir par le locataire)
 * @param message      message informatif
 */
public record InitPaiementMobileResponse(
        UUID echeanceUuid,
        BigDecimal montant,
        String paymentUrl,
        String message
) {}