package com.ipnet.rentalapi.Gbails.fedapay;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * Endpoint webhook FedaPay.
 *
 * URL à configurer dans votre dashboard FedaPay :
 *   https://votre-domaine.com/api/bail/webhook/fedapay
 *
 * Cet endpoint est PUBLIC (pas de JWT requis) mais sécurisé par
 * la vérification HMAC-SHA256 de la signature FedaPay.
 *
 * Flux :
 *   1. FedaPay envoie un POST JSON avec le header X-FEDAPAY-SIGNATURE
 *   2. On vérifie la signature via FedaPaySignatureVerifier
 *   3. On répond 200 IMMÉDIATEMENT (avant tout traitement lourd)
 *   4. On délègue le traitement à FedaPayService
 *
 * ⚠️ Répondre 200 rapidement est essentiel :
 *   FedaPay attend une réponse 2xx dans les 30s, sinon il retente jusqu'à 9 fois.
 */
@RestController
@RequestMapping("api/bail/webhook")
public class FedaPayWebhookController {

    private static final Logger log = LoggerFactory.getLogger(FedaPayWebhookController.class);

    private static final String FEDAPAY_SIGNATURE_HEADER = "X-FEDAPAY-SIGNATURE";

    private final FedaPaySignatureVerifier signatureVerifier;
    private final FedaPayService fedaPayService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FedaPayWebhookController(
            FedaPaySignatureVerifier signatureVerifier,
            FedaPayService fedaPayService
           
    ) {
        this.signatureVerifier = signatureVerifier;
        this.fedaPayService = fedaPayService;
    }

    /**
     * Reçoit les événements webhook de FedaPay.
     *
     *  Sécurité : vérification HMAC-SHA256 avant tout traitement
     *  Rapidité : réponse 200 immédiate
     *  Idempotence : gérée dans FedaPayService.traiterWebhook()
     */
    @PostMapping("/fedapay")
    public ResponseEntity<WebhookAck> recevoirWebhook(HttpServletRequest request) {

        //  1. Récupérer le raw body depuis le cache (mis en place par RawBodyCachingFilter)
        byte[] rawBody = getRawBody(request);

        //  2. Récupérer le header de signature
        String sigHeader = request.getHeader(FEDAPAY_SIGNATURE_HEADER);

        //  3. Vérifier la signature HMAC — lève une exception si invalide
        try {
            signatureVerifier.verifier(rawBody, sigHeader);
        } catch (FedaPaySignatureVerifier.FedaPaySignatureException e) {
            log.warn("[WEBHOOK] Signature invalide : {}", e.getMessage());
            return ResponseEntity.status(400).body(new WebhookAck("signature_invalide", e.getMessage()));
        }

        //  4. Parser le payload
        FedaPayWebhookPayload payload;
        try {
            payload = objectMapper.readValue(rawBody, FedaPayWebhookPayload.class);
        } catch (Exception e) {
            log.error("[WEBHOOK] Erreur parsing payload : {}", e.getMessage());
            return ResponseEntity.status(400).body(new WebhookAck("parse_error", e.getMessage()));
        }

        log.info("[WEBHOOK]  Signature OK — événement={} eventId={}",
                payload.eventName(), payload.eventId());

        //  5. Répondre 200 IMMÉDIATEMENT (bonne pratique FedaPay)
        //       Le traitement réel se fait après la réponse
        ResponseEntity<WebhookAck> response = ResponseEntity.ok(
                new WebhookAck("received", "Événement " + payload.eventId() + " reçu")
        );

        //  6. Traiter l'événement (après envoi de la réponse)
        try {
            fedaPayService.traiterWebhook(payload);
        } catch (Exception e) {
            // On logue mais on NE RELANCE PAS l'exception :
            // FedaPay a déjà reçu le 200, on ne veut pas qu'il retente pour un bug interne
            log.error("[WEBHOOK] Erreur traitement événement {} : {}",
                    payload.eventId(), e.getMessage(), e);
        }

        return response;
    }

    //  Helper : lire le raw body depuis le ContentCachingRequestWrapper 

    private byte[] getRawBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] cached = wrapper.getContentAsByteArray();
            if (cached.length > 0) {
                return cached;
            }
        }
        // Fallback si le filtre n'a pas wrappé (ne devrait pas arriver)
        try {
            return request.getInputStream().readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Impossible de lire le body de la requête webhook", e);
        }
    }

    //  DTO de réponse 

    public record WebhookAck(String status, String message) {}
}