package com.iitL32025.guniv.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.iitL32025.guniv.models.Etudiant;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
	
}
