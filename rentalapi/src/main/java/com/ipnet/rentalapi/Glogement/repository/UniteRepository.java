package com.ipnet.rentalapi.Glogement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipnet.rentalapi.Glogement.models.Unite;

public interface UniteRepository extends JpaRepository<Unite, Long>{
	Optional<Unite> findByUuid(UUID id);
}
