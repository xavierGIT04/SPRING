package com.ipnet.rentalapi.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.rentalapi.auth.dto.AuthRequest;
import com.ipnet.rentalapi.auth.dto.AuthResponse;
import com.ipnet.rentalapi.auth.dto.UtilisateurRequest;
import com.ipnet.rentalapi.auth.service.AuthService;

@RestController
@RequestMapping("api/auth/")
public class AuthController {
	
    private  AuthService authService;
    
    public AuthController(AuthService authService) {
    	this.authService = authService;
    }
    
    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(AuthRequest user){
    	return ResponseEntity.ok(authService.login(user));
    }
    
    @PostMapping("register")
    public ResponseEntity<AuthResponse> register(UtilisateurRequest user){
    	return ResponseEntity.ok(authService.register(user));
    }
    

}
