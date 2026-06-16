package com.ipnet.rentalapi.auth.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ipnet.rentalapi.auth.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	
	public JwtAuthenticationFilter(
			JwtService jwtService, 
			UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// 1. Lire le header Authorization
		final String authHeader = request.getHeader("Authorization");
		
		// 2. Si pas de token → on laisse passer (sera bloqué par SecurityConfig si route protégée)
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
		    return;
		}
		
		 // 3. Extraire le token (enlever "Bearer ")
		final String jwt = authHeader.substring(7);
		final String telephone = jwtService.extractUsername(jwt);
		
		 // 4. Si username extrait et pas encore authentifié dans le contexte
		if(telephone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			 
			// 5. Charger l'utilisateur depuis la BDD
			UserDetails userDetails = userDetailsService.loadUserByUsername(telephone);
			
			// 6. Valider le token
			if(jwtService.isTokenValid(jwt, userDetails)) {
				UsernamePasswordAuthenticationToken authToken = 
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				// 7. Enregistrer l'authentification dans le contexte Spring
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		
		filterChain.doFilter(request, response);
		
	}
	
	
	
}
