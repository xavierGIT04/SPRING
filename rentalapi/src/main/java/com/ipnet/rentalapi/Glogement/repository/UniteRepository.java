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
}
