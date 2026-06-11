package com.ipnet.rentalapi.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ipnet.rentalapi.auth.model.Utilisateur;

@Component
public class AuthUtils {
	
	public Utilisateur getUtilisateurConnecte() {
		Authentication authentication = SecurityContextHolder
				.getContext()
				.getAuthentication();
		
		if (authentication == null || !authentication.isAuthenticated() 
		            || "anonymousUser".equals(authentication.getPrincipal())) {
		      throw new SecurityException("Utilisateur non connecté");
		 }
		 
		return (Utilisateur) authentication.getPrincipal();
	}
	
	public String getTelephone() {
		return SecurityContextHolder
				.getContext()
				.getAuthentication()
				.getName();
	}

}
