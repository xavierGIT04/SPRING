package tg.ipnet.FirstSpring.Mappers;

import tg.ipnet.FirstSpring.DTO.Request.NoteDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.NoteDtoResponse;
import tg.ipnet.FirstSpring.entity.Inscription;
import tg.ipnet.FirstSpring.entity.Note;
import tg.ipnet.FirstSpring.entity.UE;
import tg.ipnet.FirstSpring.repository.InscriptionRepository;
import tg.ipnet.FirstSpring.repository.UeRepository;


public class NoteMappers {
	
	private InscriptionRepository inscriptionRepository;
	private UeRepository ueRepository;
	
	public NoteMappers(InscriptionRepository inscriptionRepository, UeRepository ueRepository) {
		this.inscriptionRepository = inscriptionRepository;
		this.ueRepository = ueRepository;
	}
	
	
	public NoteDtoResponse toNoteDto(Note n) {
		
		NoteDtoResponse noteDto = new NoteDtoResponse();
		noteDto.setId(n.getId());
		noteDto.setInscrit(n.getInscrit().getEtudiant().getNom()+"-"+n.getInscrit().getEtudiant().getPrenom());
		noteDto.setDateObtention(n.getDateObtention());
		noteDto.setTypeEvaluation(n.getTypeEvaluation());
		noteDto.setUe(n.getUe().getCodeUe()+"-"+n.getUe().getIntitule());
		noteDto.setValeurNote(n.getValeuNote());

		return noteDto;
	}
	
	public Note toNote(NoteDtoRequest n) {
		Note note = new Note();
		Inscription inscrit = inscriptionRepository.findById(n.getInscrit()).orElseThrow(()-> new RuntimeException("not found"));
		UE ue = ueRepository.findById(n.getUe()).orElseThrow(()-> new RuntimeException("not found"));

		note.setInscrit(inscrit);
		note.setDateObtention(n.getDateObtention());
		note.setTypeEvaluation(n.getTypeEvaluation());
		note.setUe(ue);
		note.setValeuNote(n.getValeurNote());
		return note;
	}


}
