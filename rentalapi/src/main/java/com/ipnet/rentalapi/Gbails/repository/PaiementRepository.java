package com.ipnet.rentalapi.Gbails.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipnet.rentalapi.Gbails.models.Paiement;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Optional<Paiement> findByUuid(String uuid);
    List<Paiement> findByEcheanceUuid(String echeanceUuid);
}
