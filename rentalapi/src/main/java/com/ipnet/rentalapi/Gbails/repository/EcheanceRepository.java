package com.ipnet.rentalapi.Gbails.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipnet.rentalapi.Gbails.Enums.StatutEcheance;
import com.ipnet.rentalapi.Gbails.models.Echeance;

public interface EcheanceRepository extends JpaRepository<Echeance, Long> {
    Optional<Echeance> findByUuid(String uuid);
    List<Echeance> findByContratBailUuid(String contratUuid);
    List<Echeance> findByStatut(StatutEcheance statut);
}