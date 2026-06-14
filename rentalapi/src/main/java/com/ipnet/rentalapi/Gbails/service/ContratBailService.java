package com.ipnet.rentalapi.Gbails.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ipnet.rentalapi.Gbails.dto.request.BailRequest;
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

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ContratBailService {

	private final ContratBailRepository cr;
	private final UniteRepository ur;
	private final UtilisateurRepository utr;
	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthUtils authUtils;
	
	public ContratBailService(ContratBailRepository cr, UniteRepository ur, UtilisateurRepository utr) {
		super();
		this.cr = cr;
		this.ur = ur;
		this.utr = utr;
		this.passwordEncoder = null;
	}
	
	
	public UtilisateurResponse addLocataire(UtilisateurRequest user) {
		Utilisateur newUser = new Utilisateur();
    	newUser.setNomComplet(user.getNom());
    	newUser.setPassword(passwordEncoder.encode(user.getCode()));
    	newUser.setUsername(user.getTelephone());
    	newUser.setRole(RoleEnum.LOCATAIRE);
    	newUser.setCreatedBy(authUtils.getUtilisateurConnecte());
    	
    	Utilisateur userdb = utr.save(newUser);
    	
    	return new UtilisateurResponse(
    			userdb.getUsername(),
    			userdb.getProfil(), 
    			userdb.getRole(), 
    			userdb.getNomComplet(), 
    			"");
    	
	}
	
	public BailResponse addContrat(BailRequest request) {
		ContratBail bail = new ContratBail();
		Utilisateur locataire = utr.findByUuid(request.locataireUuid()).orElseThrow(()-> new RuntimeException("Locataire introuvable"));
		Unite unite = ur.findByUuid(request.uniteUuid()).orElseThrow(()-> new RuntimeException("Logement introuvable"));
		
		bail.setDateDebut(request.dateDebut());

		if(request.dateSortie() != null) {
			bail.setDateSortie(request.dateSortie());
			bail.setDuree((int)(ChronoUnit.MONTHS.between(request.dateDebut(), request.dateSortie())));
		}
		bail.setConditions(request.conditions());
		bail.setLoyer(request.loyer());
		bail.setJourEcheance(request.jourEcheance());
		bail.setLocataire(locataire);
		bail.setUnite(unite);
		
		ContratBail contratdb = cr.save(bail);
		
		return new BailResponse(
				contratdb.getUuid(),
			    contratdb.getLocataire().getNomComplet(),
			    contratdb.getLocataire().getUsername(),
			    contratdb.getUnite().getCode_unite(),
			    contratdb.getUnite().getType(),
			    contratdb.getDateDebut(),
			    contratdb.getDateSortie(),
			    contratdb.getLoyer(),
			    contratdb.getStatut(),
			    contratdb.getJourEcheance(),
			    contratdb.getDuree(),
			    contratdb.getConditions()
			    
				);
		
	}
	
	
}
