package com.ipnet.rentalapi.Glogement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ipnet.rentalapi.Glogement.Enums.StatutUnite;
import com.ipnet.rentalapi.Glogement.models.Unite;

public interface UniteRepository extends JpaRepository<Unite, Long>{
	Optional<Unite> findByUuid(UUID id);
	
	 @Query("""
	            SELECT u FROM Unite u
	            WHERE u.statut = :libre
	              AND u.bien.proprietaire.uuid = :id
	            """)
	    List<Unite> findUniteLibre(@Param("id") UUID id, @Param("libre") StatutUnite libre);

	 /**
	  * Nombre total d'unités d'un propriétaire, filtrable par bien (immeuble).
	  * Utilisé par le dashboard Profil A pour le taux d'occupation.
	  */
	 @Query("""
	            SELECT COUNT(u) FROM Unite u
	            WHERE u.bien.proprietaire.uuid = :proprietaireUuid
	              AND (:bienUuid IS NULL OR u.bien.uuid = :bienUuid)
	            """)
	 long countTotalUnites(@Param("proprietaireUuid") UUID proprietaireUuid, @Param("bienUuid") UUID bienUuid);

	 /**
	  * Nombre d'unités d'un propriétaire dans un statut donné (OCCUPEE / LIBRE / MAINTENANCE),
	  * filtrable par bien (immeuble).
	  */
	 @Query("""
	            SELECT COUNT(u) FROM Unite u
	            WHERE u.bien.proprietaire.uuid = :proprietaireUuid
	              AND u.statut = :statut
	              AND (:bienUuid IS NULL OR u.bien.uuid = :bienUuid)
	            """)
	 long countUnitesByStatut(
	         @Param("proprietaireUuid") UUID proprietaireUuid,
	         @Param("statut") StatutUnite statut,
	         @Param("bienUuid") UUID bienUuid
	 );
}
