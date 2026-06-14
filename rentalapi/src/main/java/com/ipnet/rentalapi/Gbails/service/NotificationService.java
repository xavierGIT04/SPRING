package com.ipnet.rentalapi.Gbails.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.rentalapi.Gbails.dto.request.NotificationRequest;
import com.ipnet.rentalapi.Gbails.dto.response.NotificationResponse;
import com.ipnet.rentalapi.Gbails.models.Notification;
import com.ipnet.rentalapi.Gbails.repository.NotificationRepository;


@Service
@Transactional
public class NotificationService {
	
	 	private final NotificationRepository notificationRepository;
	 
	    public NotificationService(
	            NotificationRepository notificationRepository
	    ) {
	        this.notificationRepository = notificationRepository;
	     
	    }
	 
	    /**
	     * Crée et persiste une notification pour un locataire.
	     * Méthode centrale appelée par ContratBailService et EcheanceScheduler.
	     */
	    public NotificationResponse creerNotification(NotificationRequest request) {
	    	
	    	
	        
	    	Notification notification = new Notification();
	        notification.setLocataire(request.destinataire());
	        notification.setTitre(request.titre());
	        notification.setMessage(request.message());
	        notification.setDateEnvoi(LocalDateTime.now());
	        notification.setTypeNotification(request.typeNotification());
	        notification.setEstLu(false);
	        
	        
	        
	        return toResponse(notificationRepository.save(notification));
	    }
	 
	    /**
	     * Marquer une notification comme lue.
	     */
	    public NotificationResponse marquerCommeLu(UUID notifUuid) {
	        Notification notif = notificationRepository.findByUuid(notifUuid)
	                .orElseThrow(() -> new RuntimeException("Notification introuvable : " + notifUuid));
	        notif.setEstLu(true);
	        return toResponse(notificationRepository.save(notif));
	    }
	 
	    /**
	     * Retourne toutes les notifications non lues d'un locataire.
	     */
	    @Transactional(readOnly = true)
	    public List<NotificationResponse> getNonLues(UUID locataireUuid) {
	        return notificationRepository
	                .findByLocataireUuidAndEstLuFalse(locataireUuid)
	                .stream()
	                .map(this::toResponse)
	                .toList();
	    }
	 
	    private NotificationResponse toResponse(Notification n) {
	        return new NotificationResponse(
	                n.getUuid(),
	                n.getTitre(),
	                n.getMessage(),
	                n.getDateEnvoi(),
	                n.getTypeNotification(),
	                n.isEstLu()
	        );
	    }
	    
	   

}
