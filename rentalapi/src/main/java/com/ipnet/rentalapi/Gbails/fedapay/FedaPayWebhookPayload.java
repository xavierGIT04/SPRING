package com.ipnet.rentalapi.Gbails.fedapay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Représente le payload JSON envoyé par FedaPay sur l'endpoint webhook.
 *
 * Structure réelle reçue (l'entity EST directement la transaction,
 * il n'y a pas de niveau d'imbrication "transaction" supplémentaire) :
 * {
 *   "id": "evt_xxx",
 *   "name": "transaction.approved",
 *   "object": "event",
 *   "entity": {
 *     "id": 458984,
 *     "reference": "xyz-123",
 *     "amount": 50000,
 *     "status": "approved",
 *     "custom_metadata": {
 *       "echeance_uuid": "...",
 *       "locataire_uuid": "..."
 *     }
 *   }
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FedaPayWebhookPayload(

        @JsonProperty("id")
        String eventId,

        @JsonProperty("name")
        String eventName,       // "transaction.approved", "transaction.declined", ...

        @JsonProperty("object")
        String object,          // "event"

        @JsonProperty("entity")
        Transaction entity      // ✅ entity EST la transaction, pas un wrapper
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Transaction(
            @JsonProperty("id")
            Long id,

            @JsonProperty("reference")
            String reference,   // référence unique FedaPay de la transaction

            @JsonProperty("amount")
            BigDecimal amount,  // montant en FCFA

            @JsonProperty("status")
            String status,      // "approved", "declined", "canceled", "transferred"

            @JsonProperty("description")
            String description,

            @JsonProperty("custom_metadata")
            CustomMetadata customMetadata
    ) {}

    /**
     * Métadonnées personnalisées injectées lors de la création
     * de la transaction FedaPay (dans FedaPayService.initierPaiement).
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CustomMetadata(
            @JsonProperty("echeance_uuid")
            String echeanceUuid,

            @JsonProperty("locataire_uuid")
            String locataireUuid
    ) {}
}