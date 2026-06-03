package com.ipnet.rentalapi.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ipnet.rentalapi.auth.repository.UtilisateurRepository;

@Configuration
public class ApplicationConfig {

	private final UtilisateurRepository utilisateurRepository;
	
	public ApplicationConfig(UtilisateurRepository utilisateurRepository) {
		this.utilisateurRepository = utilisateurRepository;
	}
	
	@Bean
	public UserDetailsService userDetailsService() {
		return telephone -> utilisateurRepository.findByTelephone(telephone)
				  .orElseThrow(()-> new UsernameNotFoundException(
						  "Utilisateur non trouvé : " + telephone
						  ));
	}
	
	 // Encodeur de mot de passe (TOUJOURS hasher les mots de passe !)
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
	
    // AuthenticationManager : utilisé dans AuthController pour déclencher l'auth
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}
	
	
}
