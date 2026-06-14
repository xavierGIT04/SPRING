package com.ipnet.rentalapi.Gbails.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ipnet.rentalapi.Gbails.Enums.StatutEcheance;
import com.ipnet.rentalapi.Gbails.models.Echeance;

public interface EcheanceRepository extends JpaRepository<Echeance, Long> {
	 
    Optional<Echeance> findByUuid(UUID uuid);
 
    List<Echeance> findByContratBailUuid(UUID contratUuid);
 
    List<Echeance> findByStatut(StatutEcheance statut);
 
    /**
     *  IDEMPOTENCE : Vérifie si une échéance existe déjà pour un contrat
     * donné sur le même mois/année. Empêche la création de doublons
     * si le scheduler redémarre ou tourne deux fois dans le mois.
     *
     * On compare l'année et le mois de dateEcheance, pas le jour exact,
     * car le jourEcheance peut varier (ex: 28 Feb au lieu de 30 Feb).
     */
    @Query("""
            SELECT COUNT(e) > 0
            FROM Echeance e
            WHERE e.contratBail.uuid = :contratUuid
              AND YEAR(e.dateEcheance) = :annee
              AND MONTH(e.dateEcheance) = :mois
            """)
    boolean existsForContratAndMois(
            @Param("contratUuid") UUID contratUuid,
            @Param("annee") int annee,
            @Param("mois") int mois
    );
 
    /**
     * Pour afficher les échéances en retard (utile pour le scheduler de rappel).
     */
    @Query("""
            SELECT e FROM Echeance e
            WHERE e.statut IN ('EN_ATTENTE', 'PARTIELLEMENT_PAYE')
              AND e.dateEcheance < :today
            """)
    List<Echeance> findEcheancesEnRetard(@Param("today") LocalDate today);
}