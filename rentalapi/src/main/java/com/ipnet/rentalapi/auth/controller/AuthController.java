package com.ipnet.rentalapi.auth.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.rentalapi.auth.dto.AuthRequest;
import com.ipnet.rentalapi.auth.dto.AuthResponse;
import com.ipnet.rentalapi.auth.dto.UtilisateurRequest;
import com.ipnet.rentalapi.auth.model.Utilisateur;
import com.ipnet.rentalapi.auth.service.AuthService;
import com.ipnet.rentalapi.auth.service.AuthUtils;



@RestController
@RequestMapping("api/auth/")
public class AuthController {
	
    private  AuthService authService;
    @Autowired
    private AuthUtils authUtils;
    
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
    public ResponseEntity<AuthResponse> updateInfos(
    		@RequestParam String nom, 
    		@RequestParam String tel
    		){
    	Utilisateur user = authUtils.getUtilisateurConnecte();
    	return ResponseEntity.ok(authService.updateInfo(user, nom, tel));
    }
    
    @PutMapping("update_code")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateCode(@RequestParam UUID uuid, @RequestParam String code) throws Exception{
    	return ResponseEntity.ok(authService.updateCode(uuid, code));
    }
    
    @GetMapping("users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Utilisateur>> users(){
    	return ResponseEntity.ok(authService.allUsers());
    }

}
