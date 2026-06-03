package com.iitL32025.guniv.service;

import java.util.List;

import com.iitL32025.guniv.models.Etudiant;

public interface EtudiantService {
	List<Etudiant> getEtudiants();
	Etudiant add(Etudiant e);
	Etudiant getEtudiant(Long id);
	void sup(Long id);
	Etudiant putEtudiant(Etudiant e,Long id);
}
