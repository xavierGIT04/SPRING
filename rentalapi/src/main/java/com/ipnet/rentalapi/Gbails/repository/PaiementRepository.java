package com.ipnet.rentalapi.Gbails.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipnet.rentalapi.Gbails.models.Paiement;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Optional<Paiement> findByUuid(UUID uuid);
    List<Paiement> findByEcheanceUuid(UUID echeanceUuid);
}
