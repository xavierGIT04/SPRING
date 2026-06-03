package com.iitL32025.guniv.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iitL32025.guniv.models.Etudiant;
import com.iitL32025.guniv.repository.EtudiantRepository;

@Service
public class EtudiantServiceImpl implements EtudiantService{

	@Autowired
	private  EtudiantRepository etudiantRepository;
	
	@Override
	public List<Etudiant> getEtudiants() {
		return etudiantRepository.findAll();
	}

	@Override
	public Etudiant add(Etudiant e) {	
		return etudiantRepository.save(e);
	}

	@Override
	public Etudiant getEtudiant(Long id) {
		return etudiantRepository.findById(id).orElseThrow(()->new RuntimeException("Etudiant not found"));
	}

	@Override
	public void sup(Long id) {
		Etudiant etu = etudiantRepository.findById(id).orElseThrow(()->new RuntimeException("Etudiant not found"));
		etudiantRepository.delete(etu);
	}

	@Override
	public Etudiant putEtudiant(Etudiant e, Long id) {
		Etudiant etu = etudiantRepository.findById(id).orElseThrow(()->new RuntimeException("Etudiant not found"));
		etu.setAdresse(e.getAdresse());
		etu.setDateNaissance(e.getDateNaissance());
		etu.setEmail(e.getEmail());
		etu.setEstactif(e.isEstactif());
		etu.setNom(e.getNom());
		etu.setPrenom(e.getPrenom());
		etu.setTelephone(e.getTelephone());
		return etudiantRepository.save(etu);
	}

}
