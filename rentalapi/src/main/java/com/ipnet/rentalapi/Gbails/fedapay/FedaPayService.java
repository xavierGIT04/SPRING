package com.ipnet.rentalapi.Gbails.fedapay;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipnet.rentalapi.Gbails.Enums.ModePaiement;
import com.ipnet.rentalapi.Gbails.Enums.TypeNotification;
import com.ipnet.rentalapi.Gbails.dto.request.NotificationRequest;
import com.ipnet.rentalapi.Gbails.dto.request.PaiementRequest;
import com.ipnet.rentalapi.Gbails.dto.response.PaiementInitResult;
import com.ipnet.rentalapi.Gbails.models.Echeance;
import com.ipnet.rentalapi.Gbails.repository.EcheanceRepository;
import com.ipnet.rentalapi.Gbails.repository.PaiementRepository;
import com.ipnet.rentalapi.Gbails.service.EcheanceService;
import com.ipnet.rentalapi.Gbails.service.NotificationService;
import com.ipnet.rentalapi.Gbails.service.PaiementService;

/**
 * Service de communication avec l'API FedaPay.
 *
 * Deux responsabilités :
 *  1. initierPaiement()  → créer une transaction FedaPay et retourner le lien de paiement
 *  2. traiterWebhook()   → être appelé par le WebhookController après vérification de signature
 */
@Service
public class FedaPayService {

    private static final Logger log = LoggerFactory.getLogger(FedaPayService.class);

    // API FedaPay
    private static final String FEDAPAY_API_BASE_SANDBOX = "https://sandbox-api.fedapay.com/v1";
    private static final String FEDAPAY_API_BASE_LIVE    = "https://api.fedapay.com/v1";

    @Value("${fedapay.api.secret-key}")
    private String apiSecretKey;

    @Value("${fedapay.mode:sandbox}")
    private String mode;  // "sandbox" ou "live"

    @Value("${fedapay.callback.url}")
    private String callbackUrl;  // ex: https://votre-domaine.com/api/bail/webhook/fedapay

    private final EcheanceRepository echeanceRepository;
    private final PaiementService paiementService;
    private final PaiementRepository paiementRepository;
    private final EcheanceService echeanceService;
    private final NotificationService notificationService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public FedaPayService(
            EcheanceRepository echeanceRepository,
            PaiementService paiementService,
            PaiementRepository paiementRepository,
            EcheanceService echeanceService,
            NotificationService notificationService
    ) {
        this.echeanceRepository = echeanceRepository;
        this.paiementService = paiementService;
        this.paiementRepository = paiementRepository;
        this.echeanceService = echeanceService;
        this.notificationService = notificationService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    //  1. Initier un paiement Mobile Money 

    /**
     * Crée une transaction FedaPay et retourne le lien de paiement
     * (Flooz / TMoney) à rediriger vers le locataire.
     *
     * On injecte l'UUID de l'échéance dans "custom_metadata" pour pouvoir
     * retrouver l'échéance à solder lors du callback.
     *
     * @param echeanceUuid UUID de l'échéance à payer
     * @param telephone    numéro Mobile Money du locataire (format: +22891000000)
     * @return URL de paiement FedaPay à ouvrir par le locataire
     */
    public PaiementInitResult initierPaiement(UUID echeanceUuid, String telephone) {
	    Echeance echeance = echeanceRepository.findByUuid(echeanceUuid)
	            .orElseThrow(() -> new RuntimeException("Échéance introuvable : " + echeanceUuid));
	
	    String locataireNom = echeance.getContratBail().getLocataire().getNomComplet();
	    String uniteCode    = echeance.getContratBail().getUnite().getCode_unite();
	    BigDecimal montant  = echeance.getMontantRestant();
	
	    // ── Étape 1 : Créer la transaction ───────────────────────────
	    Map<String, Object> body = new HashMap<>();
	    body.put("amount",       montant.intValue());
	    body.put("currency",     Map.of("iso", "XOF"));
	    body.put("description",  "Loyer " + uniteCode + " — " + echeance.getDateEcheance());
	    body.put("callback_url", callbackUrl);
	    body.put("custom_metadata", Map.of(
	            "echeance_uuid",  echeanceUuid.toString(),
	            "locataire_uuid", echeance.getContratBail().getLocataire().getUuid().toString()
	    ));
	    String customerCountry = "sandbox".equalsIgnoreCase(mode) ? "BJ" : "TG";

	    body.put("customer", Map.of(
	            "firstname", locataireNom,
	            "phone_number", Map.of(
	                    "number",  telephone,
	                    "country", customerCountry
	            )
	    ));
	
	    try {
	        // POST /v1/transactions
	        String createUrl = getApiBase() + "/transactions";
	        HttpEntity<Map<String, Object>> requestEntity =
	                new HttpEntity<>(body, buildHeaders());
	        ResponseEntity<String> createResponse =
	                restTemplate.postForEntity(createUrl, requestEntity, String.class);
	
	        log.info("[FEDAPAY] Réponse création transaction : {}", createResponse.getBody());
	
	        System.out.println("[FEDAPAY] Réponse création transaction : {}"+ createResponse.getBody());
	        JsonNode json = objectMapper.readTree(createResponse.getBody());
	
	        JsonNode transactionNode = json.has("id") ? json : json.path("v1/transaction");
	        String transactionId = transactionNode.path("id").asText();

	        if (transactionId.isBlank() || transactionId.equals("null")) {
	            log.error("[FEDAPAY] ID absent dans la réponse : {}", json);
	            throw new RuntimeException("FedaPay n'a pas retourné d'ID de transaction");
	        }
	
	        // ── Étape 2 : Récupérer le token de paiement ─────────────
	        // GET /v1/transactions/{id}/token
	        String tokenUrl = getApiBase() + "/transactions/" + transactionId + "/token";
	        HttpEntity<Void> tokenRequest = new HttpEntity<>(buildHeaders());
	        ResponseEntity<String> tokenResponse = restTemplate.exchange(
	                tokenUrl,
	                org.springframework.http.HttpMethod.POST,
	                tokenRequest,
	                String.class
	        );
	
	        log.info("[FEDAPAY] Réponse token : {}", tokenResponse.getBody());
	
	        JsonNode tokenJson = objectMapper.readTree(tokenResponse.getBody());
	        String token = tokenJson.path("token").asText();

	        if (token.isBlank() || token.equals("null")) {
	            log.error("[FEDAPAY] Token absent : {}", tokenJson);
	            throw new RuntimeException("FedaPay n'a pas retourné de token");
	        }

	        // ── Étape 3 : Récupérer l'URL de paiement directement renvoyée par FedaPay ────
	        // (plus fiable que de la reconstruire soi-même : le format de l'URL checkout
	        //  peut évoluer côté FedaPay sans préavis)
	        String paymentUrl = tokenJson.path("url").asText();

	        if (paymentUrl.isBlank() || paymentUrl.equals("null")) {
	            log.error("[FEDAPAY] URL de paiement absente dans la réponse token : {}", tokenJson);
	            throw new RuntimeException("FedaPay n'a pas retourné d'URL de paiement");
	        }

	        log.info("[FEDAPAY] Transaction {} créée — URL : {}", transactionId, paymentUrl);

	        return new PaiementInitResult(paymentUrl, montant);
	
	    } catch (Exception e) {
	        log.error("[FEDAPAY] Erreur : {}", e.getMessage(), e);
	        throw new RuntimeException("Erreur paiement FedaPay : " + e.getMessage());
	    }
}

    // 2. Traiter le résultat du webhook 

    /**
     * Traite un événement FedaPay reçu via webhook.
     * Appelé par FedaPayWebhookController APRÈS vérification de la signature.
     *
     * Gère les événements :
     *   - transaction.approved  → solder l'échéance + notifier
     *   - transaction.declined  → notifier l'échec
     *   - transaction.canceled  → notifier l'annulation
     *   - transaction.transferred → log informatif
     *
     *  IDEMPOTENCE WEBHOOK :
     *   On vérifie si un paiement avec cette référence FedaPay existe déjà
     *   avant de solder l'échéance. Si oui → on retourne sans rien faire.
     *   Cela gère les retries FedaPay (jusqu'à 9 tentatives).
     */
    public void traiterWebhook(FedaPayWebhookPayload payload) {
        String eventName = payload.eventName();
        log.info("[FEDAPAY WEBHOOK] Événement reçu : {}", eventName);

        switch (eventName) {
            case "transaction.approved"   -> traiterApprouve(payload);
            case "transaction.declined"   -> traiterDecline(payload);
            case "transaction.canceled"   -> traiterAnnule(payload);
            case "transaction.transferred"-> log.info("[FEDAPAY WEBHOOK] Fonds transférés — ref={}",
                                                 getReference(payload));
            default -> log.debug("[FEDAPAY WEBHOOK] Événement ignoré : {}", eventName);
        }
    }

    // Handlers privés par événement 

    private void traiterApprouve(FedaPayWebhookPayload payload) {
        FedaPayWebhookPayload.Transaction tx = getTransaction(payload);
        String reference   = tx.reference();
        String echeanceUuidStr = getEcheanceUuid(payload);

        // IDEMPOTENCE : si ce paiement existe déjà, on sort immédiatement
        boolean dejaSolde = paiementRepository.findByReferencePaiement(reference).isPresent();


        if (dejaSolde) {
            log.info("[FEDAPAY WEBHOOK] Paiement {} déjà traité, skip (idempotence).", reference);
            return;
        }

        UUID echeanceUuid = UUID.fromString(echeanceUuidStr);

        // Enregistrer le paiement via PaiementService (qui met aussi à jour l'échéance)
        PaiementRequest paiementRequest = new PaiementRequest(
                echeanceUuid,
                tx.amount(),
                ModePaiement.MOBILE_MONEY,
                reference
        );
        paiementService.enregistrerPaiement(paiementRequest);

        log.info("[FEDAPAY WEBHOOK]  Paiement approuvé traité — ref={} montant={} FCFA",
                reference, tx.amount());
    }

    private void traiterDecline(FedaPayWebhookPayload payload) {
        String echeanceUuidStr = getEcheanceUuid(payload);
        if (echeanceUuidStr == null) return;

        Echeance echeance = echeanceRepository
                .findByUuid(UUID.fromString(echeanceUuidStr))
                .orElse(null);

        if (echeance == null) return;

        String message = String.format(
                "Bonjour %s, votre tentative de paiement Mobile Money pour l'unité %s a échoué. " +
                "Montant : %.0f FCFA. Référence : %s. Veuillez réessayer.",
                echeance.getContratBail().getLocataire().getNomComplet(),
                echeance.getContratBail().getUnite().getCode_unite(),
                getTransaction(payload).amount(),
                getReference(payload)
        );
        
        notificationService.creerNotification(
        		new NotificationRequest(
               		 echeance.getContratBail().getLocataire(),
                        "❌ Paiement Mobile Money échoué",
                        message,
                        TypeNotification.ALERTE_RETARD
                        )
        );

        log.warn("[FEDAPAY WEBHOOK] ❌ Paiement refusé — ref={}", getReference(payload));
    }

    private void traiterAnnule(FedaPayWebhookPayload payload) {
        String echeanceUuidStr = getEcheanceUuid(payload);
        if (echeanceUuidStr == null) return;

        Echeance echeance = echeanceRepository
                .findByUuid(UUID.fromString(echeanceUuidStr))
                .orElse(null);

        if (echeance == null) return;

        String message = String.format(
                "Bonjour %s, votre paiement Mobile Money pour l'unité %s a été annulé. " +
                "Référence : %s.",
                echeance.getContratBail().getLocataire().getNomComplet(),
                echeance.getContratBail().getUnite().getCode_unite(),
                getReference(payload)
        );
       
        notificationService.creerNotification(
        		 new NotificationRequest(
                		 echeance.getContratBail().getLocataire(),
                         "Paiement annulé",
                         message,
                         TypeNotification.ALERTE_RETARD
                	)
        		);

        log.info("[FEDAPAY WEBHOOK] Paiement annulé — ref={}", getReference(payload));
    }

    //  Helpers 

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiSecretKey);
        return headers;
    }

    private String getApiBase() {
        return "sandbox".equalsIgnoreCase(mode) ? FEDAPAY_API_BASE_SANDBOX : FEDAPAY_API_BASE_LIVE;
    }

    private FedaPayWebhookPayload.Transaction getTransaction(FedaPayWebhookPayload payload) {
        if (payload.entity() == null || payload.entity().transaction() == null) {
            throw new IllegalArgumentException("Payload FedaPay sans transaction : " + payload.eventId());
        }
        return payload.entity().transaction();
    }

    private String getEcheanceUuid(FedaPayWebhookPayload payload) {
        try {
            return getTransaction(payload).customMetadata().echeanceUuid();
        } catch (Exception e) {
            log.warn("[FEDAPAY WEBHOOK] custom_metadata.echeance_uuid absent dans {}", payload.eventId());
            return null;
        }
    }

    private String getReference(FedaPayWebhookPayload payload) {
        try {
            return getTransaction(payload).reference();
        } catch (Exception e) {
            return payload.eventId();
        }
    }
}