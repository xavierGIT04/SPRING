package tg.ipnet.FirstSpring.Mappers;



import tg.ipnet.FirstSpring.DTO.Request.InscriptionDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.InscriptionDtoResponse;
import tg.ipnet.FirstSpring.entity.AnneeScolaire;
import tg.ipnet.FirstSpring.entity.Etudiant;
import tg.ipnet.FirstSpring.entity.Inscription;
import tg.ipnet.FirstSpring.entity.Parcours;
import tg.ipnet.FirstSpring.repository.AnneeScolaireRepository;
import tg.ipnet.FirstSpring.repository.EtudiantRepository;
import tg.ipnet.FirstSpring.repository.ParcoursRepository;

public class InscriptionMapper {
	
	private AnneeScolaireRepository anneeScolaireRepository;
	private EtudiantRepository etudiantRepository;
	private ParcoursRepository parcoursRepository;

	public InscriptionMapper(AnneeScolaireRepository anneeScolaireRepository,
			EtudiantRepository etudiantRepository,
			ParcoursRepository parcoursRepository) {
		this.anneeScolaireRepository = anneeScolaireRepository;
		this.etudiantRepository = etudiantRepository;
		this.parcoursRepository = parcoursRepository;

	}
	
	public InscriptionDtoResponse toInscriptionDto(Inscription i) {
		InscriptionDtoResponse inscriptionDto = new InscriptionDtoResponse();
		inscriptionDto.setId(i.getId());
		inscriptionDto.setEtudiant(i.getEtudiant().getNom()+"-"+i.getEtudiant().getPrenom());
		inscriptionDto.setMatricule(i.getEtudiant().getMatricule());
		inscriptionDto.setFiliere(i.getParcours().getFiliere().getNomFiliere());
		inscriptionDto.setParcours(i.getParcours().getLibelle());
		inscriptionDto.setAnneeScolaire(i.getAnnee().getDateDebut().getYear()+"-"+i.getAnnee().getDateFin().getYear());
		inscriptionDto.setDateInscription(i.getDateInscription());
		inscriptionDto.setEtatInscription(i.getEtatInscription());
		return inscriptionDto;
	}
	
	public Inscription toInscription(InscriptionDtoRequest i) {
		Inscription inscription = new Inscription();
		
		AnneeScolaire annee = anneeScolaireRepository.findById(i.getAnneeScolaire()).orElseThrow(()-> new RuntimeException("not found"));
		Etudiant etudiant = etudiantRepository.findById(i.getEtudiant()).orElseThrow(()-> new RuntimeException("not found"));
		Parcours parcours = parcoursRepository.findById(i.getParcours()).orElseThrow(()-> new RuntimeException("not found"));

		
		inscription.setAnnee(annee);
		inscription.setDateInscription(i.getDateInscription());
		inscription.setEtudiant(etudiant);
		inscription.setEtatInscription(i.getEtatInscription());
		inscription.setParcours(parcours);
		
		return inscription;
	}

}
