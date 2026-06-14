package com.ipnet.rentalapi.Gbails.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.rentalapi.Gbails.Enums.TypeNotification;
import com.ipnet.rentalapi.Gbails.dto.request.NotificationRequest;
import com.ipnet.rentalapi.Gbails.dto.request.PaiementRequest;
import com.ipnet.rentalapi.Gbails.dto.response.PaiementResponse;
import com.ipnet.rentalapi.Gbails.models.Echeance;
import com.ipnet.rentalapi.Gbails.models.Paiement;
import com.ipnet.rentalapi.Gbails.repository.EcheanceRepository;
import com.ipnet.rentalapi.Gbails.repository.PaiementRepository;

@Service
@Transactional
public class PaiementService {
	
	private final PaiementRepository paiementRepository;
    private final EcheanceRepository echeanceRepository;
    private final EcheanceService echeanceService;
    private final NotificationService notificationService;
 
    public PaiementService(
            PaiementRepository paiementRepository,
            EcheanceRepository echeanceRepository,
            EcheanceService echeanceService,
            NotificationService notificationService
    ) {
        this.paiementRepository = paiementRepository;
        this.echeanceRepository = echeanceRepository;
        this.echeanceService = echeanceService;
        this.notificationService = notificationService;
    }
 
    /**
     * Enregistre un paiement (espèces ou Mobile Money) pour une échéance.
     *
     * Pour un paiement FedaPay (Mobile Money), la referencePaiement contient
     * l'identifiant de transaction retourné par FedaPay. Le webhook FedaPay
     * peut aussi appeler ce service directement.
     */
    public PaiementResponse enregistrerPaiement(PaiementRequest request) {
        Echeance echeance = echeanceRepository.findByUuid(request.echeanceUuid())
                .orElseThrow(() -> new RuntimeException("Échéance introuvable : " + request.echeanceUuid()));
 
       
        // Créer l'enregistrement du paiement
        Paiement paiement = new Paiement();
        paiement.setEcheance(echeance);
        paiement.setMontant(request.montant());
        paiement.setModePaiement(request.modePaiement());
        paiement.setReferencePaiement(request.referencePaiement());
        paiement.setDatePaiement(LocalDateTime.now());
 
        Paiement saved = paiementRepository.save(paiement);
 
        // Mettre à jour l'échéance (montant restant + statut)
        echeanceService.appliquerPaiement(request.echeanceUuid(), request.montant());
 
        // Notification de confirmation au locataire
        String locataireNom = echeance.getContratBail().getLocataire().getNomComplet();
        String uniteCode = echeance.getContratBail().getUnite().getCode_unite();
        String messageNotif = String.format(
                "Bonjour %s, votre paiement de %.0f FCFA pour l'unité %s a bien été enregistré. " +
                "Référence : %s.",
                locataireNom,
                request.montant(),
                uniteCode,
                request.referencePaiement() != null ? request.referencePaiement() : "N/A"
        );
        
        NotificationRequest notifRequest = new NotificationRequest(
        		echeance.getContratBail().getLocataire(),
                "Paiement confirmé",
                messageNotif,
                TypeNotification.CONFIRMATION_PAIEMENT
        		
        		);
        
        notificationService.creerNotification(notifRequest);
 
        return toResponse(saved);
    }
 
    /**
     * Historique des paiements d'une échéance.
     */
    @Transactional(readOnly = true)
    public List<PaiementResponse> getPaiementsByEcheance(UUID echeanceUuid) {
        return paiementRepository
                .findByEcheanceUuid(echeanceUuid)
                .stream()
                .map(this::toResponse)
                .toList();
    }
 
    //  Mapper 
 
    private PaiementResponse toResponse(Paiement p) {
        return new PaiementResponse(
                p.getUuid(),
                p.getDatePaiement(),
                p.getMontant(),
                p.getModePaiement().name(),
                p.getReferencePaiement(),
                p.getRecuUrl()
        );
    }

}
