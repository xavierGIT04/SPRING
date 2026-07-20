package com.ipnet.rentalapi.GdashboardClassique.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 *
 * - tauxOccupation : ratio unités occupées / total, en pourcentage.
 * - revenuAttendu   : total des loyers dus sur la période (échéances émises).
 * - revenuEncaisse  : total réellement encaissé sur la période (revenu net).
 * - montantImpaye   : total restant dû sur les échéances non soldées de la période.
 */
public record DashboardProfilAResponse(
		int annee,
		Integer mois,
		UUID bienUuid,
		UUID uniteUuid,

		long totalUnites,
		long unitesOccupees,
		long unitesLibres,
		long unitesMaintenance,
		double tauxOccupation,

		BigDecimal revenuAttendu,
		BigDecimal revenuEncaisse,
		BigDecimal montantImpaye,

		long nombreImpayes,
		List<ImpayeResponse> impayes
) {}
