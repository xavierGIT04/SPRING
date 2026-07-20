package com.ipnet.rentalapi.Gbails.repository;

import java.math.BigDecimal;
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
    
    /**
     * Liste des impayés en cours (EN_ATTENTE, PARTIELLEMENT_PAYE, EN_RETARD)
     * pour un propriétaire, filtrable par bien et/ou unité. Utilisé par le
     * dashboard Profil A (Module A5).
     */
    @Query("""
            SELECT e FROM Echeance e
            WHERE e.contratBail.unite.bien.proprietaire.uuid = :proprietaireUuid
              AND e.statut IN ('EN_ATTENTE', 'PARTIELLEMENT_PAYE', 'EN_RETARD')
              AND (:bienUuid IS NULL OR e.contratBail.unite.bien.uuid = :bienUuid)
              AND (:uniteUuid IS NULL OR e.contratBail.unite.uuid = :uniteUuid)
            ORDER BY e.dateEcheance ASC
            """)
    List<Echeance> findImpayesByProprietaire(
            @Param("proprietaireUuid") UUID proprietaireUuid,
            @Param("bienUuid") UUID bienUuid,
            @Param("uniteUuid") UUID uniteUuid
    );

    /**
     * Somme des loyers attendus (montantDu) sur une période donnée (année, mois optionnel),
     * pour un propriétaire, filtrable par bien et/ou unité.
     */
    @Query("""
            SELECT COALESCE(SUM(e.montantDu), 0) FROM Echeance e
            WHERE e.contratBail.unite.bien.proprietaire.uuid = :proprietaireUuid
              AND (:bienUuid IS NULL OR e.contratBail.unite.bien.uuid = :bienUuid)
              AND (:uniteUuid IS NULL OR e.contratBail.unite.uuid = :uniteUuid)
              AND YEAR(e.dateEcheance) = :annee
              AND (:mois IS NULL OR MONTH(e.dateEcheance) = :mois)
            """)
    BigDecimal sumMontantDu(
            @Param("proprietaireUuid") UUID proprietaireUuid,
            @Param("bienUuid") UUID bienUuid,
            @Param("uniteUuid") UUID uniteUuid,
            @Param("annee") int annee,
            @Param("mois") Integer mois
    );

    /**
     * Somme des montants restants dus (impayés) sur une période donnée,
     * pour un propriétaire, filtrable par bien et/ou unité.
     */
    @Query("""
            SELECT COALESCE(SUM(e.montantRestant), 0) FROM Echeance e
            WHERE e.contratBail.unite.bien.proprietaire.uuid = :proprietaireUuid
              AND e.statut IN ('EN_ATTENTE', 'PARTIELLEMENT_PAYE', 'EN_RETARD')
              AND (:bienUuid IS NULL OR e.contratBail.unite.bien.uuid = :bienUuid)
              AND (:uniteUuid IS NULL OR e.contratBail.unite.uuid = :uniteUuid)
              AND YEAR(e.dateEcheance) = :annee
              AND (:mois IS NULL OR MONTH(e.dateEcheance) = :mois)
            """)
    BigDecimal sumMontantImpayePeriode(
            @Param("proprietaireUuid") UUID proprietaireUuid,
            @Param("bienUuid") UUID bienUuid,
            @Param("uniteUuid") UUID uniteUuid,
            @Param("annee") int annee,
            @Param("mois") Integer mois
    );

}