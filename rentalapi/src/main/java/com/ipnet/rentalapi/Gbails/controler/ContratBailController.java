package com.ipnet.rentalapi.Gbails.controler;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.rentalapi.Gbails.dto.request.BailRequest;
import com.ipnet.rentalapi.Gbails.dto.request.EcheanceRequest;
import com.ipnet.rentalapi.Gbails.dto.request.InitPaiementMobileRequest;
import com.ipnet.rentalapi.Gbails.dto.request.PaiementRequest;
import com.ipnet.rentalapi.Gbails.dto.response.BailResponse;
import com.ipnet.rentalapi.Gbails.dto.response.EcheanceResponse;
import com.ipnet.rentalapi.Gbails.dto.response.InitPaiementMobileResponse;
import com.ipnet.rentalapi.Gbails.dto.response.NotificationResponse;
import com.ipnet.rentalapi.Gbails.dto.response.PaiementInitResult;
import com.ipnet.rentalapi.Gbails.dto.response.PaiementResponse;
import com.ipnet.rentalapi.Gbails.fedapay.FedaPayService;
import com.ipnet.rentalapi.Gbails.service.ContratBailService;
import com.ipnet.rentalapi.Gbails.service.EcheanceService;
import com.ipnet.rentalapi.Gbails.service.NotificationService;
import com.ipnet.rentalapi.Gbails.service.PaiementService;
import com.ipnet.rentalapi.Glogement.dto.response.UniteResponse;
import com.ipnet.rentalapi.auth.dto.UtilisateurRequest;
import com.ipnet.rentalapi.auth.dto.UtilisateurResponse;
import com.ipnet.rentalapi.auth.model.Utilisateur;
import com.ipnet.rentalapi.auth.repository.UtilisateurRepository;
import com.ipnet.rentalapi.auth.service.AuthUtils;

@RestController
@RequestMapping("api/bail/")
public class ContratBailController {

    private final ContratBailService contratBailService;
    private final EcheanceService echeanceService;
    private final PaiementService paiementService;
    private final NotificationService notificationService;
    private final AuthUtils authUtils;
    private final FedaPayService fedaPayService;
    private final UtilisateurRepository  ur;
    public ContratBailController(
            ContratBailService contratBailService,
            EcheanceService echeanceService,
            PaiementService paiementService,
            NotificationService notificationService,
            AuthUtils authUtils,
            UtilisateurRepository  ur,
            FedaPayService fedaPayService   // NOUVEAU
    ) {
        this.contratBailService = contratBailService;
        this.echeanceService = echeanceService;
        this.paiementService = paiementService;
        this.notificationService = notificationService;
        this.authUtils = authUtils;
        this.ur = ur;
        this.fedaPayService = fedaPayService;   // NOUVEAU
    }
    
    // Locataires

    @PostMapping("add_locataire")
    public ResponseEntity<UtilisateurResponse> addLocataire(@RequestBody UtilisateurRequest request) {
        return ResponseEntity.ok(contratBailService.addLocataire(request));
    }
    
    @GetMapping("mes_locataires")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public ResponseEntity<List<UtilisateurResponse>> mesLocataires() {
    	
    	Utilisateur userDb = ur.findByTelephone(authUtils.getTelephone()).orElseThrow();

    	List<UtilisateurResponse> response = userDb.getLocatairesCrees()
    	        .stream()
    	        .map(u -> new UtilisateurResponse(
    	                u.getUsername(),
    	                u.getProfil(),
    	                u.getRole(),
    	                u.getNomComplet(), 
    	                u.getAvatar(),
    	                u.getUuid()
    	        )
    	        		)
    	        .toList();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("unite_libre")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public ResponseEntity<List<UniteResponse>> getUniteLibre(){
    	return ResponseEntity.ok(contratBailService.getUniteLibre());
    }

    // Contrats 

    @PostMapping("add")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public ResponseEntity<BailResponse> creerContrat(@RequestBody BailRequest request) {
        return ResponseEntity.ok(contratBailService.addContrat(request));
    }

    @GetMapping("mes_contrats")
    @PreAuthorize("hasRole('PROPRIETAIRE') or hasRole('LOCATAIRE')")
    public ResponseEntity<List<BailResponse>> mesContrats() {
        UUID locataireUuid = authUtils.getUtilisateurConnecte().getUuid();
        return ResponseEntity.ok(contratBailService.getContratsByLocataire(locataireUuid));
    }
    
    @GetMapping("proprio_contrats")
    @PreAuthorize("hasRole('PROPRIETAIRE') or hasRole('LOCATAIRE')")
    public ResponseEntity<List<BailResponse>> proprioContrats() {
        UUID id = authUtils.getUtilisateurConnecte().getUuid();
        return ResponseEntity.ok(contratBailService.getContratsByProprietaire(id));
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
    
    @GetMapping("echeances_retard")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public ResponseEntity<List<EcheanceResponse>> getEcheancesEnretard() {
        return ResponseEntity.ok(echeanceService.echeanceEnRetard());
    }
    
    @PostMapping("echeances/")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public ResponseEntity<EcheanceResponse> addEcheance(@RequestBody EcheanceRequest request) {
        return ResponseEntity.ok(echeanceService.creerEcheance(request));
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
    
    // Paiement Mobile Money 

    /**
     * Initie un paiement Mobile Money (Flooz/TMoney) via FedaPay.
     * Retourne un lien de paiement à ouvrir par le locataire.
     *
     * Le locataire clique sur le lien → FedaPay collecte son consentement
     * → FedaPay notifie le backend via le webhook /api/bail/webhook/fedapay
     * → Le paiement est enregistré automatiquement.
     */
    @PostMapping("initier_paiement_mobile")
    @PreAuthorize("hasRole('LOCATAIRE')")
    public ResponseEntity<InitPaiementMobileResponse> initierPaiementMobile(
            @RequestBody InitPaiementMobileRequest request) {

        // Récupérer le téléphone du locataire connecté si non fourni
        String telephone = (request.telephone() != null && !request.telephone().isBlank())
                ? request.telephone()
                : authUtils.getUtilisateurConnecte().getUsername();

        PaiementInitResult result = fedaPayService.initierPaiement(
                request.echeanceUuid(), telephone);

        return ResponseEntity.ok(new InitPaiementMobileResponse(
                request.echeanceUuid(),
                result.montant(),
                result.paymentUrl(),
                "Cliquez sur le lien pour finaliser votre paiement Mobile Money."
        ));
    }

    //  Notifications 

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