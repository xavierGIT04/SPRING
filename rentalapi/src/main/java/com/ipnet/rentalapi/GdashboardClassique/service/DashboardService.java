package com.ipnet.rentalapi.GdashboardClassique.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.rentalapi.Gbails.models.Echeance;
import com.ipnet.rentalapi.Gbails.repository.EcheanceRepository;
import com.ipnet.rentalapi.Gbails.repository.PaiementRepository;
import com.ipnet.rentalapi.GdashboardClassique.dto.response.DashboardProfilAResponse;
import com.ipnet.rentalapi.GdashboardClassique.dto.response.ImpayeResponse;
import com.ipnet.rentalapi.Glogement.Enums.StatutUnite;
import com.ipnet.rentalapi.Glogement.repository.UniteRepository;

@Service
@Transactional(readOnly = true)
public class DashboardService {

	private final UniteRepository uniteRepository;
	private final EcheanceRepository echeanceRepository;
	private final PaiementRepository paiementRepository;

	public DashboardService(
			UniteRepository uniteRepository,
			EcheanceRepository echeanceRepository,
			PaiementRepository paiementRepository
	) {
		this.uniteRepository = uniteRepository;
		this.echeanceRepository = echeanceRepository;
		this.paiementRepository = paiementRepository;
	}

	/**
	 * Construit le dashboard Profil A pour le propriétaire connecté.
	 *
	 * @param proprietaireUuid propriétaire connecté (issu du token JWT)
	 * @param bienUuid   filtre optionnel par immeuble
	 * @param uniteUuid  filtre optionnel par appartement
	 * @param annee      année de la période (défaut : année en cours)
	 * @param mois       mois de la période, 1-12 (optionnel, sinon période = année entière)
	 */
	public DashboardProfilAResponse getDashboard(
			UUID proprietaireUuid,
			UUID bienUuid,
			UUID uniteUuid,
			Integer annee,
			Integer mois
	) {
		int anneeEffective = (annee != null) ? annee : LocalDate.now().getYear();

		// Taux d'occupation
		long total = uniteRepository.countTotalUnites(proprietaireUuid, bienUuid);
		long occupees = uniteRepository.countUnitesByStatut(proprietaireUuid, StatutUnite.OCCUPEE, bienUuid);
		long libres = uniteRepository.countUnitesByStatut(proprietaireUuid, StatutUnite.LIBRE, bienUuid);
		long maintenance = uniteRepository.countUnitesByStatut(proprietaireUuid, StatutUnite.MAINTENANCE, bienUuid);

		double taux = total > 0
				? BigDecimal.valueOf(occupees * 100.0 / total).setScale(2, RoundingMode.HALF_UP).doubleValue()
				: 0.0;

		// Revenus (attendu vs réellement encaissé sur la période)
		BigDecimal revenuAttendu = echeanceRepository.sumMontantDu(
				proprietaireUuid, bienUuid, uniteUuid, anneeEffective, mois);
		BigDecimal revenuEncaisse = paiementRepository.sumEncaissements(
				proprietaireUuid, bienUuid, uniteUuid, anneeEffective, mois);
		BigDecimal montantImpaye = echeanceRepository.sumMontantImpayePeriode(
				proprietaireUuid, bienUuid, uniteUuid, anneeEffective, mois);

		// Impayés en cours (toutes périodes confondues, pour ne rien manquer)
		List<ImpayeResponse> impayes = echeanceRepository
				.findImpayesByProprietaire(proprietaireUuid, bienUuid, uniteUuid)
				.stream()
				.map(this::toImpayeResponse)
				.toList();

		return new DashboardProfilAResponse(
				anneeEffective,
				mois,
				bienUuid,
				uniteUuid,
				total,
				occupees,
				libres,
				maintenance,
				taux,
				revenuAttendu,
				revenuEncaisse,
				montantImpaye,
				impayes.size(),
				impayes
		);
	}

	private ImpayeResponse toImpayeResponse(Echeance e) {
		long joursDeRetard = Math.max(0, ChronoUnit.DAYS.between(e.getDateEcheance(), LocalDate.now()));

		return new ImpayeResponse(
				e.getContratBail().getUuid(),
				e.getUuid(),
				e.getContratBail().getLocataire().getNomComplet(),
				e.getContratBail().getLocataire().getUsername(),
				e.getContratBail().getUnite().getCode_unite(),
				e.getDateEcheance(),
				e.getMontantRestant(),
				e.getStatut(),
				joursDeRetard
		);
	}
}
