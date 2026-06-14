package com.ipnet.rentalapi.Gbails.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipnet.rentalapi.Gbails.Enums.BailEnum;
import com.ipnet.rentalapi.Gbails.models.ContratBail;

public interface ContratBailRepository extends JpaRepository<ContratBail, Long> {
	Optional<ContratBail> findByUuid(UUID uuid);
    List<ContratBail> findByStatut(BailEnum statut);
    List<ContratBail> findByLocataireUuid(UUID locataireUuid);
}
