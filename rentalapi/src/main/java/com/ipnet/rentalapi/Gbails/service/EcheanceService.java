package com.ipnet.rentalapi.Gbails.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.rentalapi.Gbails.Enums.StatutEcheance;
import com.ipnet.rentalapi.Gbails.Enums.TypeNotification;
import com.ipnet.rentalapi.Gbails.dto.request.EcheanceRequest;
import com.ipnet.rentalapi.Gbails.dto.request.NotificationRequest;
import com.ipnet.rentalapi.Gbails.dto.response.EcheanceResponse;
import com.ipnet.rentalapi.Gbails.models.ContratBail;
import com.ipnet.rentalapi.Gbails.models.Echeance;
import com.ipnet.rentalapi.Gbails.repository.ContratBailRepository;
import com.ipnet.rentalapi.Gbails.repository.EcheanceRepository;

@Service
@Transactional
public class EcheanceService {
 
    private final EcheanceRepository echeanceRepository;
    @Autowired
    private  NotificationService notificationService;
    private final ContratBailRepository contratBailRepository;
 
    public EcheanceService(EcheanceRepository echeanceRepository,  ContratBailRepository contratBailRepository) {
        this.echeanceRepository = echeanceRepository;
		this.contratBailRepository = contratBailRepository;
    }
 
    /**
     * Crée une échéance pour un contrat donné à une date donnée.
     * Appelé par le scheduler — la vérification d'idempotence est faite AVANT.
     */
    public EcheanceResponse creerEcheance(EcheanceRequest request) {
        Echeance echeance = new Echeance();
        ContratBail contrat = contratBailRepository.findByUuid(request.contrat_id()).orElseThrow(()->new RuntimeException("Contrat introuvable"));
        echeance.setContratBail(contrat); 
        echeance.setDateEcheance(request.dateEcheance());
        echeance.setMontantDu(contrat.getLoyer());
        echeance.setMontantRestant(contrat.getLoyer());
        echeance.setStatut(StatutEcheance.EN_ATTENTE);
        
        String message = String.format(
                "Bonjour %s, votre loyer de %.0f FCFA pour l'unité %s est à régler avant le %s.",
                contrat.getLocataire().getNomComplet(),
                contrat.getLoyer(),
                contrat.getUnite().getCode_unite(),
                request.dateEcheance()
        );
       
        notificationService.creerNotification(
        		 new NotificationRequest(
        	        		contrat.getLocataire(),
        	                "Rappel de loyer — " + request.dateEcheance().getMonth().name(),
        	                message,
        	                TypeNotification.RELANCE_LOYER
        	          )
        		);

        return toResponse(echeanceRepository.save(echeance));
    }
 
    /**
     * Vérifie si une échéance existe déjà pour ce contrat sur ce mois/année.
     *  Clé d'idempotence : (contratUuid, annee, mois).
     */
    public boolean echeanceExisteDeja(UUID contratUuid, int annee, int mois) {
        return echeanceRepository.existsForContratAndMois(contratUuid, annee, mois);
    }
 
    /**
     * Récupère toutes les échéances d'un contrat.
     */
    @Transactional(readOnly = true)
    public List<EcheanceResponse> getEcheancesByContrat(UUID contratUuid) {
        return echeanceRepository
                .findByContratBailUuid(contratUuid)
                .stream()
                .map(this::toResponse)
                .toList();
    }
 
    /**
     * Applique un paiement sur une échéance et met à jour son statut.
     * Appelé par PaiementService après enregistrement du paiement.
     *
     * @param echeanceUuid UUID de l'échéance
     * @param montantVerse montant versé lors de ce paiement
     */
    public void appliquerPaiement(UUID echeanceUuid, BigDecimal montantVerse) {
        Echeance echeance = echeanceRepository.findByUuid(echeanceUuid)
                .orElseThrow(() -> new RuntimeException("Échéance introuvable : " + echeanceUuid));
 
        BigDecimal restantAvant = echeance.getMontantRestant();
        BigDecimal nouveauRestant = restantAvant.subtract(montantVerse);
 
        if (nouveauRestant.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Le montant versé (" + montantVerse + ") dépasse le restant dû (" + restantAvant + ")"
            );
        }
 
        echeance.setMontantRestant(nouveauRestant);
 
        if (nouveauRestant.compareTo(BigDecimal.ZERO) == 0) {
            echeance.setStatut(StatutEcheance.PAYE);
        } else {
            echeance.setStatut(StatutEcheance.PARTIELLEMENT_PAYE);
        }
 
        echeanceRepository.save(echeance);
    }
    
    public List<EcheanceResponse> echeanceEnRetard(){
    	List<Echeance> eEnRetard = echeanceRepository.findEcheancesEnRetard(LocalDate.now());
    	eEnRetard.forEach(e -> e.setStatut(StatutEcheance.EN_RETARD));
    	List<EcheanceResponse> response = eEnRetard
    	        .stream()
    	        .map(u -> new EcheanceResponse(
    	        		u.getUuid(),
    	        	    u.getDateEcheance(),
    	        	    u.getMontantDu(),
    	        	    u.getMontantRestant(),
    	        	    u.getStatut(),
    	        	    u.getContratBail().getUnite().getCode_unite()     
    	        ))
    	        .toList();
    	return response;
    }
 
    /**
     * Marque les échéances non payées dont la date est dépassée comme EN_RETARD.
     * Appelé par le scheduler de relance.
     */
    public int marquerEcheancesEnRetard() {
        List<Echeance> enRetard = echeanceRepository.findEcheancesEnRetard(LocalDate.now());
        enRetard.forEach(e -> e.setStatut(StatutEcheance.EN_RETARD));
        echeanceRepository.saveAll(enRetard);
        return enRetard.size();
    }
 
    // Mapper 
 
    private EcheanceResponse toResponse(Echeance e) {
        return new EcheanceResponse(
                e.getUuid(),
                e.getDateEcheance(),
                e.getMontantDu(),
                e.getMontantRestant(),
                e.getStatut(),
                e.getContratBail().getUnite().getCode_unite()
        );
    }
}