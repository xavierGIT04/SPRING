package com.ipnet.rentalapi.Gbails.fedapay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

/**
 * Vérifie l'authenticité d'un webhook FedaPay.
 *
 * FedaPay envoie la signature dans le header X-FEDAPAY-SIGNATURE.
 * Le format du header est : "t=<timestamp>,v=<hmac_sha256_hex>"
 *
 * Algorithme de vérification :
 *   1. Extraire le timestamp (t) et la signature (v) du header
 *   2. Vérifier que le timestamp n'est pas trop ancien (anti-replay)
 *   3. Recalculer HMAC-SHA256(secret, timestamp + "." + rawBody)
 *   4. Comparer en temps constant (anti-timing-attack)
 */
@Component
public class FedaPaySignatureVerifier {

    private static final Logger log = LoggerFactory.getLogger(FedaPaySignatureVerifier.class);

    // Tolérance de 5 minutes pour les timestamps (protection replay)
    private static final long TOLERANCE_SECONDS = 300L;

    @Value("${fedapay.webhook.secret}")
    private String webhookSecret;
    
    @Value("${fedapay.mode:sandbox}")
    private String mode;

    /**
     * Vérifie la signature du webhook FedaPay.
     *
     * @param rawBody   corps brut de la requête HTTP (bytes non parsés)
     * @param sigHeader valeur du header X-FEDAPAY-SIGNATURE
     * @throws FedaPaySignatureException si la signature est invalide ou le timestamp trop ancien
     */
    public void verifier(byte[] rawBody, String sigHeader) {

    // ── MODE SANDBOX : bypass signature ──────────────────────────
    if ("sandbox".equalsIgnoreCase(mode)) {
        log.warn("[FEDAPAY] Mode sandbox — vérification signature DÉSACTIVÉE");
        return;
    }

    // ── MODE LIVE : vérification complète ────────────────────────
    if (sigHeader == null || sigHeader.isBlank()) {
        throw new FedaPaySignatureException("Header X-FEDAPAY-SIGNATURE absent ou vide");
    }

    String timestamp = null;
    String signatureRecue = null;

    for (String part : sigHeader.split(",")) {
        if (part.startsWith("t=")) timestamp = part.substring(2).trim();
        else if (part.startsWith("v=")) signatureRecue = part.substring(2).trim();
    }

    if (timestamp == null || signatureRecue == null) {
        throw new FedaPaySignatureException(
            "Format du header X-FEDAPAY-SIGNATURE invalide. Attendu : t=<ts>,v=<sig>"
        );
    }

    long ts;
    try {
        ts = Long.parseLong(timestamp);
    } catch (NumberFormatException e) {
        throw new FedaPaySignatureException("Timestamp invalide : " + timestamp);
    }

    long maintenant = Instant.now().getEpochSecond();
    if (Math.abs(maintenant - ts) > TOLERANCE_SECONDS) {
        throw new FedaPaySignatureException(
            "Webhook rejeté : timestamp trop ancien (" + ts + ")"
        );
    }

    String rawBodyStr = new String(rawBody, StandardCharsets.UTF_8);
    String payloadSigne = timestamp + "." + rawBodyStr;
    String signatureCalculee = hmacSha256(webhookSecret, payloadSigne);

    boolean valide = MessageDigest.isEqual(
        signatureCalculee.getBytes(StandardCharsets.UTF_8),
        signatureRecue.getBytes(StandardCharsets.UTF_8)
    );

    if (!valide) {
        log.warn("[FEDAPAY] Signature invalide. Reçue={} Calculée={}", 
            signatureRecue, signatureCalculee);
        throw new FedaPaySignatureException("Signature FedaPay invalide.");
    }

    log.debug("[FEDAPAY] Signature vérifiée avec succès (timestamp={})", timestamp);
}

    // Calcul HMAC-SHA256 

    private String hmacSha256(String secret, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"
            );
            mac.init(keySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Erreur interne HMAC-SHA256", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    //  Exception métier 

    public static class FedaPaySignatureException extends RuntimeException {
        public FedaPaySignatureException(String message) {
            super(message);
        }
    }
}