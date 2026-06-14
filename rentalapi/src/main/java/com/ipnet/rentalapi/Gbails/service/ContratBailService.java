package com.ipnet.rentalapi.Gbails.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.rentalapi.Gbails.Enums.BailEnum;
import com.ipnet.rentalapi.Gbails.Enums.TypeNotification;
import com.ipnet.rentalapi.Gbails.dto.request.BailRequest;
import com.ipnet.rentalapi.Gbails.dto.request.NotificationRequest;
import com.ipnet.rentalapi.Gbails.dto.response.BailResponse;
import com.ipnet.rentalapi.Gbails.models.ContratBail;
import com.ipnet.rentalapi.Gbails.repository.ContratBailRepository;
import com.ipnet.rentalapi.Glogement.models.Unite;
import com.ipnet.rentalapi.Glogement.repository.UniteRepository;
import com.ipnet.rentalapi.auth.RoleEnum;
import com.ipnet.rentalapi.auth.dto.UtilisateurRequest;
import com.ipnet.rentalapi.auth.dto.UtilisateurResponse;
import com.ipnet.rentalapi.auth.model.Utilisateur;
import com.ipnet.rentalapi.auth.repository.UtilisateurRepository;
import com.ipnet.rentalapi.auth.service.AuthUtils;


@Service
@Transactional
public class ContratBailService {
 
    private final ContratBailRepository contratBailRepository;
    private final UniteRepository uniteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final AuthUtils authUtils;
 
    
    public ContratBailService(
            ContratBailRepository contratBailRepository,
            UniteRepository uniteRepository,
            UtilisateurRepository utilisateurRepository,
            PasswordEncoder passwordEncoder,
            NotificationService notificationService,
            AuthUtils authUtils
    ) {
        this.contratBailRepository = contratBailRepository;
        this.uniteRepository = uniteRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.authUtils = authUtils;
    }
 
    /**
     * Crée un locataire et l'associe au propriétaire connecté.
     */
    public UtilisateurResponse addLocataire(UtilisateurRequest user) {
        Utilisateur newUser = new Utilisateur();
        newUser.setNomComplet(user.getNom());
        newUser.setPassword(passwordEncoder.encode(user.getCode()));
        newUser.setUsername(user.getTelephone());
        newUser.setRole(RoleEnum.LOCATAIRE);
        newUser.setCreatedBy(authUtils.getUtilisateurConnecte());
 
        Utilisateur saved = utilisateurRepository.save(newUser);
 
        return new UtilisateurResponse(
                saved.getUsername(),
                saved.getProfil(),
                saved.getRole(),
                saved.getNomComplet(),
                ""
        );
    }
    
   
    /**
     * Crée un contrat de bail et envoie immédiatement une notification
     * de bienvenue au locataire.
     */
    public BailResponse addContrat(BailRequest request) {
        Utilisateur locataire = utilisateurRepository
                .findByUuid(request.locataireUuid())
                .orElseThrow(() -> new RuntimeException("Locataire introuvable : " + request.locataireUuid()));
 
        Unite unite = uniteRepository
                .findByUuid(request.uniteUuid())
                .orElseThrow(() -> new RuntimeException("Unité introuvable : " + request.uniteUuid()));
 
        ContratBail bail = new ContratBail();
        bail.setDateDebut(request.dateDebut());
        bail.setLocataire(locataire);
        bail.setUnite(unite);
        bail.setLoyer(request.loyer());
        bail.setJourEcheance(request.jourEcheance());
        bail.setConditions(request.conditions());
 
        if (request.dateSortie() != null) {
            bail.setDateSortie(request.dateSortie());
            int duree = (int) ChronoUnit.MONTHS.between(request.dateDebut(), request.dateSortie());
            bail.setDuree(duree);
        }
 
        ContratBail saved = contratBailRepository.save(bail);
 
        // Notification immédiate à la création du contrat
        String messageNotif = String.format(
                "Bonjour %s, votre contrat de bail pour l'unité %s a été créé avec succès. " +
                "Loyer mensuel : %.0f FCFA, échéance le %d de chaque mois.",
                locataire.getNomComplet(),
                unite.getCode_unite(),
                request.loyer(),
                request.jourEcheance()
        );
        
        NotificationRequest notifRequest = new NotificationRequest(
        		 locataire,
                 "Contrat de bail créé",
                 messageNotif,
                 TypeNotification.INFO
        		
        		);
        
        notificationService.creerNotification(notifRequest);
 
        return toResponse(saved);
    }
 
    /**
     * Récupère tous les contrats actifs (utilisé par le scheduler).
     */
    @Transactional(readOnly = true)
    public List<ContratBail> getContratsActifs() {
        return contratBailRepository.findByStatut(BailEnum.ACTIF);
    }
 
    /**
     * Récupère tous les contrats d'un locataire.
     */
    @Transactional(readOnly = true)
    public List<BailResponse> getContratsByLocataire(UUID locataireUuid) {
        return contratBailRepository
                .findByLocataireUuid(locataireUuid)
                .stream()
                .map(this::toResponse)
                .toList();
    }
 
    /**
     * Résiliation d'un contrat.
     */
    public BailResponse resilierContrat(UUID contratUuid) {
        ContratBail contrat = contratBailRepository
                .findByUuid(contratUuid)
                .orElseThrow(() -> new RuntimeException("Contrat introuvable : " + contratUuid));
 
        contrat.setStatut(BailEnum.EN_COURS_DE_RESILIATION);
        contrat.setDateSortie(LocalDate.now());
 
        // Notification de résiliation
        
        notificationService.creerNotification(
        		
        		 new NotificationRequest(
        	        	   contrat.getLocataire(),
        	               "Résiliation de contrat",
        	               "Votre contrat de bail est en cours de résiliation. " +
        	               "Veuillez prendre contact avec votre propriétaire.",
        	               TypeNotification.ALERTE_RETARD      		
        	       		)
               
        		);
 
        return toResponse(contratBailRepository.save(contrat));
    }
 
    //  Mapper 
 
    private BailResponse toResponse(ContratBail c) {
        return new BailResponse(
                c.getUuid(),
                c.getLocataire().getNomComplet(),
                c.getLocataire().getUsername(),
                c.getUnite().getCode_unite(),
                c.getUnite().getType(),
                c.getDateDebut(),
                c.getDateSortie(),
                c.getLoyer(),
                c.getStatut(),
                c.getJourEcheance(),
                c.getDuree(),
                c.getConditions()
        );
    }
}
 