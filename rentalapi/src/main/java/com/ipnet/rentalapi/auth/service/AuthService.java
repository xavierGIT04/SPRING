package com.ipnet.rentalapi.auth.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ipnet.rentalapi.auth.dto.AuthRequest;
import com.ipnet.rentalapi.auth.dto.AuthResponse;
import com.ipnet.rentalapi.auth.dto.UtilisateurRequest;
import com.ipnet.rentalapi.auth.dto.UtilisateurResponse;
import com.ipnet.rentalapi.auth.model.Utilisateur;
import com.ipnet.rentalapi.auth.repository.UtilisateurRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthService {

	private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService ;
   
    
    
    public AuthService(AuthenticationManager authenticationManager, UtilisateurRepository utilisateurRepository,
			PasswordEncoder passwordEncoder, JwtService jwtService) {
		super();
		this.authenticationManager = authenticationManager;
		this.utilisateurRepository = utilisateurRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public AuthResponse login(AuthRequest auth) {
    	
    	 // 1. Spring vérifie téléphone + codeProprietaire
    	authenticationManager.authenticate(
    			new UsernamePasswordAuthenticationToken(auth.getTelephone(), auth.getCodeProprietaire())
    	);
    	
    	 // 2. Charger l'utilisateur
    	Utilisateur utilisateur = utilisateurRepository.findByTelephone(auth.getTelephone()).orElseThrow();
    	
    	 // 3. Générer le token JWT
    	String token = jwtService.generateToken(utilisateur);
    	String refreshToken = jwtService.generateRefreshToken(utilisateur);
    	
    	return new AuthResponse(
    				utilisateur.getUuid(),
    				token,
    				utilisateur.getNomComplet(),
    				utilisateur.getRole(),
    				utilisateur.getProfil(),
    				utilisateur.getUsername(),
    				refreshToken	
    		   );
    }
    
    public AuthResponse register(UtilisateurRequest user) {
    	Utilisateur newUser = new Utilisateur();
    	newUser.setNomComplet(user.getNom());
    	newUser.setPassword(passwordEncoder.encode(user.getCode()));
    	newUser.setUsername(user.getTelephone());
    	newUser.setProfil(user.getProfil());
    	newUser.setRole(user.getRole());
    	
    	
    	Utilisateur userdb = utilisateurRepository.save(newUser);
    	
    	String token = jwtService.generateToken(userdb);
    	String refreshToken = jwtService.generateRefreshToken(userdb);
    	AuthResponse response = new AuthResponse(
    			userdb.getUuid(), 
    			token, 
    			userdb.getNomComplet(), 
    			userdb.getRole(), 
    			userdb.getProfil(),
    			userdb.getUsername(),
    			refreshToken
    			);
    	return response;
    	
    }
    
    public UtilisateurResponse  updateInfo(Utilisateur user, Map<String, String> userInfos) {
    	
    	user.setNomComplet(userInfos.get("nom"));
    	user.setUsername(userInfos.get("telephone"));
    	user.setPassword(userInfos.get("code"));
    	user.setProfil(user.getProfil());
    	user.setRole(user.getRole());
    	utilisateurRepository.save(user);
    
    	return new UtilisateurResponse(
    			user.getUsername(),
    			user.getProfil(), 
    			user.getRole(),
    			user.getNomComplet(),
    			user.getAvatar()
    		);
    	
    }
    
    public UtilisateurResponse updateAvatar(Utilisateur user, String url) {
    	user.setAvatar(url);
    	utilisateurRepository.save(user);
    	return new UtilisateurResponse(
    			user.getUsername(),
    			user.getProfil(), 
    			user.getRole(),
    			user.getNomComplet(),
    			user.getAvatar()
    		);

    }

    public List<Utilisateur> allUsers(){
    	return utilisateurRepository.findAll();
    }
    
    public UtilisateurResponse getInfos(Utilisateur user) {
    	return new UtilisateurResponse(user.getUsername(), user.getProfil(), user.getRole(), user.getNomComplet(), user.getAvatar());
    }
	
    
   
    
}
