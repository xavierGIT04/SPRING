package com.wake.GreenBank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wake.GreenBank.entity.Client;


import java.util.Optional;
import java.util.UUID;


public interface ClientRepository extends JpaRepository<Client, Long>{
	Optional<Client> findByPublicId(UUID publicId);
}
