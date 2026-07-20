package com.ipnet.rentalapi.GdashboardClassique.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.rentalapi.GdashboardClassique.dto.response.DashboardProfilAResponse;
import com.ipnet.rentalapi.GdashboardClassique.service.DashboardService;
import com.ipnet.rentalapi.auth.service.AuthUtils;

@RestController
@RequestMapping("api/dashboard/")
public class DashboardController {

	private final DashboardService dashboardService;
	private final AuthUtils authUtils;

	public DashboardController(DashboardService dashboardService, AuthUtils authUtils) {
		this.dashboardService = dashboardService;
		this.authUtils = authUtils;
	}

	/**
	 * Dashboard & rapports — Profil A (Module A5).
	 *
	 * GET /api/dashboard/profil-a
	 * GET /api/dashboard/profil-a?annee=2026
	 * GET /api/dashboard/profil-a?annee=2026&mois=7
	 * GET /api/dashboard/profil-a?bienUuid=...&annee=2026&mois=7
	 * GET /api/dashboard/profil-a?uniteUuid=...
	 *
	 * Tous les filtres sont optionnels. Sans "annee", l'année en cours est utilisée.
	 * Sans "mois", les chiffres portent sur l'année entière.
	 */
	@GetMapping("profil-a")
	@PreAuthorize("hasRole('PROPRIETAIRE')")
	public ResponseEntity<DashboardProfilAResponse> getDashboardProfilA(
			@RequestParam(required = false) UUID bienUuid,
			@RequestParam(required = false) UUID uniteUuid,
			@RequestParam(required = false) Integer annee,
			@RequestParam(required = false) Integer mois
	) {
		UUID proprietaireUuid = authUtils.getUtilisateurConnecte().getUuid();

		return ResponseEntity.ok(
				dashboardService.getDashboard(proprietaireUuid, bienUuid, uniteUuid, annee, mois)
		);
	}
}
