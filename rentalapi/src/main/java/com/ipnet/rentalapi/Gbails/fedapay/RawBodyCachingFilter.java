package com.ipnet.rentalapi.Gbails.fedapay;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * Filtre Spring qui enveloppe la requête HTTP dans un ContentCachingRequestWrapper
 * pour permettre la lecture multiple du body.
 *
 * ⚠️ POURQUOI CE FILTRE EST INDISPENSABLE :
 * Spring lit le body de la requête UNE SEULE FOIS via un InputStream.
 * Quand on utilise @RequestBody dans le controller, Jackson consomme ce stream.
 * Si on essaie ensuite de lire le rawBody pour la vérification HMAC → stream vide → signature invalide.
 *
 * Ce filtre met en cache le body dès son arrivée, avant tout traitement.
 * On applique ce filtre UNIQUEMENT sur /api/bail/webhook/** pour ne pas impacter les autres routes.
 */
@Component
public class RawBodyCachingFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // N'appliquer ce filtre qu'aux endpoints webhook
        return !request.getRequestURI().contains("/webhook/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // ContentCachingRequestWrapper permet de lire le body plusieurs fois
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        // ⚠️ Il faut forcer la lecture du body ICI pour qu'il soit mis en cache
        // avant que le controller ne le lise via @RequestBody
        wrappedRequest.getInputStream().readAllBytes();

        filterChain.doFilter(wrappedRequest, response);
    }
}