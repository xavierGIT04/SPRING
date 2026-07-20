package com.ipnet.rentalapi.Gbails.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ipnet.rentalapi.Gbails.models.Paiement;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Optional<Paiement> findByUuid(UUID uuid);
    List<Paiement> findByEcheanceUuid(UUID echeanceUuid);
    Optional<Paiement> findByReferencePaiement(String referencePaiement);
    
    /**
     * Somme des paiements réellement encaissés sur une période donnée
     * (année, mois optionnel), pour un propriétaire, filtrable par bien et/ou unité.
     * Utilisé par le dashboard Profil A (Module A5 — revenus nets).
     */
    @Query("""
            SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p
            WHERE p.echeance.contratBail.unite.bien.proprietaire.uuid = :proprietaireUuid
              AND (:bienUuid IS NULL OR p.echeance.contratBail.unite.bien.uuid = :bienUuid)
              AND (:uniteUuid IS NULL OR p.echeance.contratBail.unite.uuid = :uniteUuid)
              AND YEAR(p.datePaiement) = :annee
              AND (:mois IS NULL OR MONTH(p.datePaiement) = :mois)
            """)
    BigDecimal sumEncaissements(
            @Param("proprietaireUuid") UUID proprietaireUuid,
            @Param("bienUuid") UUID bienUuid,
            @Param("uniteUuid") UUID uniteUuid,
            @Param("annee") int annee,
            @Param("mois") Integer mois
    );

}
