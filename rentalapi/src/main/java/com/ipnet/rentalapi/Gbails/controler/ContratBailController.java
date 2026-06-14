package com.ipnet.rentalapi.Gbails.controler;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ipnet.rentalapi.Gbails.dto.request.BailRequest;
import com.ipnet.rentalapi.Gbails.dto.request.PaiementRequest;
import com.ipnet.rentalapi.Gbails.dto.response.BailResponse;
import com.ipnet.rentalapi.Gbails.dto.response.EcheanceResponse;
import com.ipnet.rentalapi.Gbails.dto.response.NotificationResponse;
import com.ipnet.rentalapi.Gbails.dto.response.PaiementResponse;
import com.ipnet.rentalapi.Gbails.service.ContratBailService;
import com.ipnet.rentalapi.Gbails.service.EcheanceService;
import com.ipnet.rentalapi.Gbails.service.NotificationService;
import com.ipnet.rentalapi.Gbails.service.PaiementService;
import com.ipnet.rentalapi.auth.dto.UtilisateurRequest;
import com.ipnet.rentalapi.auth.dto.UtilisateurResponse;
import com.ipnet.rentalapi.auth.service.AuthUtils;

@RestController
@RequestMapping("api/bail/")
@CrossOrigin("http://localhost:4200/")
public class ContratBailController {

    private final ContratBailService contratBailService;
    private final EcheanceService echeanceService;
    private final PaiementService paiementService;
    private final NotificationService notificationService;
    private final AuthUtils authUtils;

    public ContratBailController(
            ContratBailService contratBailService,
            EcheanceService echeanceService,
            PaiementService paiementService,
            NotificationService notificationService,
            AuthUtils authUtils
    ) {
        this.contratBailService = contratBailService;
        this.echeanceService = echeanceService;
        this.paiementService = paiementService;
        this.notificationService = notificationService;
        this.authUtils = authUtils;
    }

    // ─── Locataires ──────────────────────────────────────────────────────────

    @PostMapping("add_locataire")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public ResponseEntity<UtilisateurResponse> addLocataire(@RequestBody UtilisateurRequest request) {
        return ResponseEntity.ok(contratBailService.addLocataire(request));
    }

    // ─── Contrats ─────────────────────────────────────────────────────────────

    @PostMapping("add")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public ResponseEntity<BailResponse> creerContrat(@RequestBody BailRequest request) {
        return ResponseEntity.ok(contratBailService.addContrat(request));
    }

    @GetMapping("mes_contrats")
    @PreAuthorize("hasRole('LOCATAIRE')")
    public ResponseEntity<List<BailResponse>> mesContrats() {
        UUID locataireUuid = authUtils.getUtilisateurConnecte().getUuid();
        return ResponseEntity.ok(contratBailService.getContratsByLocataire(locataireUuid));
    }

    @PatchMapping("resilier/{contratUuid}")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public ResponseEntity<BailResponse> resilier(@PathVariable UUID contratUuid) {
        return ResponseEntity.ok(contratBailService.resilierContrat(contratUuid));
    }

    // ─── Échéances ────────────────────────────────────────────────────────────

    @GetMapping("echeances/{contratUuid}")
    @PreAuthorize("hasRole('PROPRIETAIRE') or hasRole('LOCATAIRE')")
    public ResponseEntity<List<EcheanceResponse>> getEcheances(@PathVariable UUID contratUuid) {
        return ResponseEntity.ok(echeanceService.getEcheancesByContrat(contratUuid));
    }

    // ─── Paiements ────────────────────────────────────────────────────────────

    @PostMapping("paiement")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public ResponseEntity<PaiementResponse> enregistrerPaiement(@RequestBody PaiementRequest request) {
        return ResponseEntity.ok(paiementService.enregistrerPaiement(request));
    }

    @GetMapping("paiements/{echeanceUuid}")
    @PreAuthorize("hasRole('PROPRIETAIRE') or hasRole('LOCATAIRE')")
    public ResponseEntity<List<PaiementResponse>> getPaiements(@PathVariable UUID echeanceUuid) {
        return ResponseEntity.ok(paiementService.getPaiementsByEcheance(echeanceUuid));
    }

    // ─── Notifications ────────────────────────────────────────────────────────

    @GetMapping("notifications/non-lues")
    @PreAuthorize("hasRole('LOCATAIRE')")
    public ResponseEntity<List<NotificationResponse>> getNotificationsNonLues() {
        UUID locataireUuid = authUtils.getUtilisateurConnecte().getUuid();
        return ResponseEntity.ok(notificationService.getNonLues(locataireUuid));
    }

    @PatchMapping("notifications/{notifUuid}/lire")
    @PreAuthorize("hasRole('LOCATAIRE')")
    public ResponseEntity<NotificationResponse> marquerLue(@PathVariable UUID notifUuid) {
        return ResponseEntity.ok(notificationService.marquerCommeLu(notifUuid));
    }
}