package com.tg.myapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tg.myapi.models.Livre;

public interface LivreRepository extends JpaRepository<Livre, Long>{
	
}
