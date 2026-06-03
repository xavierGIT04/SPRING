package tg.ipnet.FirstSpring.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import tg.ipnet.FirstSpring.DTO.Request.NoteDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.NoteDtoResponse;
import tg.ipnet.FirstSpring.Mappers.NoteMappers;
import tg.ipnet.FirstSpring.entity.Note;
import tg.ipnet.FirstSpring.repository.InscriptionRepository;
import tg.ipnet.FirstSpring.repository.NoteRepository;
import tg.ipnet.FirstSpring.repository.UeRepository;

@Service
public class NoteServiceImp implements NoteService {

	private final NoteRepository noteRepository;
	private final InscriptionRepository inscriptionRepository;
	private final UeRepository ueRepository;
	
	public NoteServiceImp(NoteRepository noteRepository, InscriptionRepository inscriptionRepository, UeRepository ueRepository) {	
		this.inscriptionRepository = inscriptionRepository;
		this.noteRepository = noteRepository;
		this.ueRepository = ueRepository;
	}
	@Override
	public NoteDtoResponse create(NoteDtoRequest n) {
		Note note = new Note();
		NoteMappers nmap = new NoteMappers(inscriptionRepository, ueRepository);
		
		note = nmap.toNote(n);
		
		return nmap.toNoteDto(noteRepository.save(note));
	}

	@Override
	public NoteDtoResponse update(NoteDtoRequest n, Long id) {
		Note note = noteRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		

		NoteMappers nmap = new NoteMappers(inscriptionRepository, ueRepository);

		Note _note = nmap.toNote(n);
		note.setInscrit(_note.getInscrit());
		note.setUe(_note.getUe());
		note.setTypeEvaluation(_note.getTypeEvaluation());
		note.setValeuNote(_note.getValeuNote());
		note.setDateObtention(_note.getDateObtention());

		return nmap.toNoteDto(noteRepository.save(note));
	}

	@Override
	public void delete(Long id) {
		Note note = noteRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		
		noteRepository.delete(note);
	}

	@Override
	public List<NoteDtoResponse> list() {
		NoteMappers nmap = new NoteMappers(inscriptionRepository, ueRepository);
		
		List<Note> lesNotes = noteRepository.findAll();
		List<NoteDtoResponse> lesNotesDto = new ArrayList<NoteDtoResponse>();

		for (Note n : lesNotes) {
			lesNotesDto.add(nmap.toNoteDto(n));
		}
		return lesNotesDto;
	}

	@Override
	public NoteDtoResponse getNote(Long id) {
		Note note = noteRepository.findById(id).orElseThrow(()-> new RuntimeException("not found"));		
		NoteMappers nmap = new NoteMappers(inscriptionRepository, ueRepository);
		return nmap.toNoteDto(note);
	}

}
