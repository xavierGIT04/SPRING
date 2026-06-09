package com.ipnet.rentalapi.Glogement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipnet.rentalapi.Glogement.models.Bien;

public interface BienRepository extends JpaRepository<Bien, Long>{
	Optional<Bien> findByUuid(UUID id);
	List<Bien> findBiensByProprietaireTelephone(String telephone);
	
}
