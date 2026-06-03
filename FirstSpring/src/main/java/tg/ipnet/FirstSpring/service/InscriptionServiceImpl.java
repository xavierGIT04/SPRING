package tg.ipnet.FirstSpring.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import tg.ipnet.FirstSpring.DTO.Request.InscriptionDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.InscriptionDtoResponse;
import tg.ipnet.FirstSpring.Mappers.InscriptionMapper;
import tg.ipnet.FirstSpring.entity.Inscription;
import tg.ipnet.FirstSpring.repository.AnneeScolaireRepository;
import tg.ipnet.FirstSpring.repository.EtudiantRepository;
import tg.ipnet.FirstSpring.repository.InscriptionRepository;
import tg.ipnet.FirstSpring.repository.ParcoursRepository;

@Service
public class InscriptionServiceImpl implements InscriptionService{

	private final InscriptionRepository inscriptionRepository;
	private final AnneeScolaireRepository anneeScolaireRepository;
	private final EtudiantRepository etudiantRepository;
	private final ParcoursRepository parcoursRepository;
	
	public InscriptionServiceImpl(InscriptionRepository inscriptionRepository,
			AnneeScolaireRepository anneeScolaireRepository,
			EtudiantRepository etudiantRepository,
			ParcoursRepository parcoursRepository) {
		this.anneeScolaireRepository = anneeScolaireRepository;
		this.etudiantRepository = etudiantRepository;
		this.inscriptionRepository = inscriptionRepository;
		this.parcoursRepository = parcoursRepository;
	}
	
	@Override
	public InscriptionDtoResponse create(InscriptionDtoRequest i) {
		Inscription inscription = new Inscription();
		InscriptionMapper imap = new InscriptionMapper(anneeScolaireRepository, etudiantRepository, parcoursRepository);
		
		inscription = imap.toInscription(i);
		
		return imap.toInscriptionDto(inscriptionRepository.save(inscription));
	}

	@Override
	public InscriptionDtoResponse update(InscriptionDtoRequest i, Long id) {
		Inscription inscrit = inscriptionRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		

		InscriptionMapper imap = new InscriptionMapper(anneeScolaireRepository, etudiantRepository, parcoursRepository);

		Inscription _inscrit = imap.toInscription(i);
		inscrit.setAnnee(_inscrit.getAnnee());
		inscrit.setDateInscription(_inscrit.getDateInscription());
		inscrit.setEtatInscription(_inscrit.getEtatInscription());

		inscrit.setParcours(_inscrit.getParcours());
		inscrit.setEtudiant(_inscrit.getEtudiant());

		return imap.toInscriptionDto(inscriptionRepository.save(inscrit));
	}

	@Override
	public void delete(Long id) {
		Inscription inscrit = inscriptionRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		
		inscriptionRepository.delete(inscrit);
		
	}

	@Override
	public List<InscriptionDtoResponse> list() {
		InscriptionMapper imap = new InscriptionMapper(anneeScolaireRepository, etudiantRepository, parcoursRepository);
		
		List<Inscription> lesInscrits = inscriptionRepository.findAll();
		List<InscriptionDtoResponse> inscritsDto = new ArrayList<InscriptionDtoResponse>();

		for (Inscription i : lesInscrits) {
			inscritsDto.add(imap.toInscriptionDto(i));
		}
		return inscritsDto;
	}

	@Override
	public InscriptionDtoResponse getInscrit(Long id) {
		InscriptionMapper imap = new InscriptionMapper(anneeScolaireRepository, etudiantRepository, parcoursRepository);
		Inscription inscrit = inscriptionRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		
		
		return imap.toInscriptionDto(inscrit);
	}

}
