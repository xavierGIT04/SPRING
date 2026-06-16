package com.ipnet.rentalapi.auth.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.rentalapi.auth.dto.AuthRequest;
import com.ipnet.rentalapi.auth.dto.AuthResponse;
import com.ipnet.rentalapi.auth.dto.UtilisateurRequest;
import com.ipnet.rentalapi.auth.dto.UtilisateurResponse;
import com.ipnet.rentalapi.auth.model.Utilisateur;
import com.ipnet.rentalapi.auth.service.AuthService;
import com.ipnet.rentalapi.auth.service.AuthUtils;
import com.ipnet.rentalapi.auth.service.JwtService;



@RestController
@RequestMapping("api/auth/")
@CrossOrigin("*")
public class AuthController {
	
    private  AuthService authService;
    @Autowired
    private AuthUtils authUtils;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;
    
    public AuthController(AuthService authService) {
    	this.authService = authService;
    }
    
    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest user){
    	return ResponseEntity.ok(authService.login(user));
    }
    
    @PostMapping("register")
    public ResponseEntity<AuthResponse> register(@RequestBody UtilisateurRequest user){
    	return ResponseEntity.ok(authService.register(user));
    }
    
    @PutMapping("update_info")
    @PreAuthorize("hasRole('LOCATAIRE') or hasRole('PROPRIETAIRE')")
    public ResponseEntity<UtilisateurResponse> updateInfos(@RequestBody HashMap<String, String> infos){
    	Utilisateur user = authUtils.getUtilisateurConnecte();
    	return ResponseEntity.ok(authService.updateInfo(user, infos));
    }
    
    @GetMapping("me")
    @PreAuthorize("hasRole('LOCATAIRE') or hasRole('PROPRIETAIRE')")
    public ResponseEntity<UtilisateurResponse> getMe(){
    	Utilisateur user = authUtils.getUtilisateurConnecte();
    	return ResponseEntity.ok(authService.getInfos(user));
    	
    }
    
    @PutMapping("update_avatar")
    @PreAuthorize("hasRole('LOCATAIRE') or hasRole('PROPRIETAIRE')")
    public ResponseEntity<UtilisateurResponse> updateAvatar(@RequestBody String url){
    	Utilisateur user = authUtils.getUtilisateurConnecte();
    	return ResponseEntity.ok(authService.updateAvatar(user, url));
    }
    
    @GetMapping("users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Utilisateur>> users(){
    	return ResponseEntity.ok(authService.allUsers());
    }
    
    @PostMapping("refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Refresh token manquant"));
        }

        try {
            // Extraire l'email depuis le refresh token
            String telephone = jwtService.extractUsername(refreshToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(telephone);

            // Valider le refresh token
            if (!jwtService.isTokenValid(refreshToken, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh token invalide ou expiré"));
            }

            // Générer un nouvel access token
            String newAccessToken = jwtService.generateToken(userDetails);

            return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "expiresIn", 900
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Refresh token invalide"));
        }
    }
    
    @PostMapping("logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));
    }

}
