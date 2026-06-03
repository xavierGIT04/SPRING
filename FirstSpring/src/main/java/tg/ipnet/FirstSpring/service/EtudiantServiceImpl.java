package tg.ipnet.FirstSpring.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import tg.ipnet.FirstSpring.DTO.Request.EtudiantDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.EtudiantDtoResponse;
import tg.ipnet.FirstSpring.Mappers.EtudiantMappers;
import tg.ipnet.FirstSpring.entity.Etudiant;
import tg.ipnet.FirstSpring.repository.EtudiantRepository;

@Service
public class EtudiantServiceImpl implements EtudiantService {

	
	private final EtudiantRepository etudiantRepository;
	
	public EtudiantServiceImpl(EtudiantRepository etudiantRepository) {
		this.etudiantRepository = etudiantRepository;
	}
	
	@Override
	public EtudiantDtoResponse create(EtudiantDtoRequest e) {
		EtudiantMappers etudMappers = new EtudiantMappers();
		Etudiant etud = new Etudiant();
		etud = etudMappers.toEtudiant(e);
		return etudMappers.toEtudiantdto(etudiantRepository.save(etud));
		
	}

	@Override
	public EtudiantDtoResponse update(EtudiantDtoRequest e, Long id) {
		Etudiant etudiant = etudiantRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		
		
		if(!etudiant.getNom().equalsIgnoreCase(e.getNom())) {
			etudiant.setNom(e.getNom());
		}
		if(!etudiant.getPrenom().equalsIgnoreCase(e.getPrenom())) {
			etudiant.setPrenom(e.getPrenom());
		}
		if(!etudiant.getMatricule().equalsIgnoreCase(e.getMatricule())) {
			etudiant.setMatricule(e.getMatricule());
		}
		if(!etudiant.getAdresse().equalsIgnoreCase(e.getAdresse())) {
			etudiant.setAdresse(e.getAdresse());
		}
		if(!etudiant.getDateNaissance().equals(e.getDateNaissance())) {
			etudiant.setDateNaissance(e.getDateNaissance());
		}
		if(!etudiant.getTelephone().equalsIgnoreCase(e.getTelephone())) {
			etudiant.setTelephone(e.getTelephone());
		}
		if(!etudiant.getEmail().equalsIgnoreCase(e.getEmail())) {
			etudiant.setEmail(e.getEmail());
		}
		
		EtudiantMappers etudMappers = new EtudiantMappers();
		return etudMappers.toEtudiantdto(etudiantRepository.save(etudiant));
		
	}

	@Override
	public void delete(Long id) {
		Etudiant etudiant = etudiantRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));
		etudiantRepository.delete(etudiant);
	}

	@Override
	public List<EtudiantDtoResponse> list() {
	    List<Etudiant>	etudiants = etudiantRepository.findAll();
	    List<EtudiantDtoResponse> etudiantsdto = new ArrayList<>();

		EtudiantMappers etudMappers = new EtudiantMappers();

	    for (Etudiant etudiant : etudiants) {
			etudiantsdto.add(etudMappers.toEtudiantdto(etudiant));
		}
		return etudiantsdto;
	}

	@Override
	public EtudiantDtoResponse getEtudiant(Long id) {
		Etudiant etudiant = etudiantRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));
		EtudiantMappers etudMappers = new EtudiantMappers();
		
		return etudMappers.toEtudiantdto(etudiant);
	}

}
