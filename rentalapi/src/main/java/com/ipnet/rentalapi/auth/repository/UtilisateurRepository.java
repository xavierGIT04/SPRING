package com.ipnet.rentalapi.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipnet.rentalapi.auth.model.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long>{
	Optional<Utilisateur> findByTelephone(String telephone);
	Optional<Utilisateur> findByUuid(UUID uuid);
}
