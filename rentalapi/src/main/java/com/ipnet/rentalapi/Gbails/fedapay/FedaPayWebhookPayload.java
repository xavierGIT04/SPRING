package com.ipnet.rentalapi.Gbails.fedapay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Représente le payload JSON envoyé par FedaPay sur l'endpoint webhook.
 *
 * Structure réelle reçue :
 * {
 *   "id": "evt_xxx",
 *   "name": "transaction.approved",
 *   "object": "event",
 *   "entity": {
 *     "id": 42,
 *     "klass": "Transaction",
 *     "transaction": {
 *       "id": 42,
 *       "reference": "xyz-123",
 *       "amount": 50000,
 *       "status": "approved",
 *       "description": "Loyer Janvier 2025",
 *       "custom_metadata": {
 *         "echeance_uuid": "...",
 *         "locataire_uuid": "..."
 *       }
 *     }
 *   }
 * }
 *
 * on passe nos propres métadonnées dans "custom_metadata" lors
 * de la création de la transaction côté FedaPay, ce qui permet de
 * retrouver l'échéance à solder lors du callback.
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
        Entity entity
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Entity(
            @JsonProperty("id")
            Long id,

            @JsonProperty("klass")
            String klass,       // "Transaction"

            @JsonProperty("transaction")
            Transaction transaction
    ) {}

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
     * Métadonnées personnalisées que vous injectez lors de la création
     * de la transaction FedaPay côté backend (dans FedaPayService.initierPaiement).
     * Elles vous permettent de retrouver l'échéance à solder.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CustomMetadata(
            @JsonProperty("echeance_uuid")
            String echeanceUuid,    // UUID de l'Echeance dans votre base

            @JsonProperty("locataire_uuid")
            String locataireUuid    // UUID du locataire (pour log / audit)
    ) {}
}