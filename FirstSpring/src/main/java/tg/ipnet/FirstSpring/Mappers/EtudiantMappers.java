package tg.ipnet.FirstSpring.Mappers;

import tg.ipnet.FirstSpring.DTO.Request.EtudiantDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.EtudiantDtoResponse;
import tg.ipnet.FirstSpring.entity.Etudiant;

public class EtudiantMappers {
	
	public Etudiant toEtudiant(EtudiantDtoRequest etdto) {
		Etudiant e = new Etudiant();
		e.setNom(etdto.getNom());
		e.setPrenom(etdto.getPrenom());
		e.setDateNaissance(etdto.getDateNaissance());
		e.setTelephone(etdto.getTelephone());
		e.setEmail(etdto.getEmail());
		e.setMatricule(etdto.getMatricule());
		e.setAdresse(etdto.getAdresse());
		return e;
	}
	
	public EtudiantDtoResponse toEtudiantdto(Etudiant e) {
		EtudiantDtoResponse etdto = new EtudiantDtoResponse();
		
		etdto.setId(e.getId());
		etdto.setNom(e.getNom());
		etdto.setPrenom(e.getPrenom());
		etdto.setDateNaissance(e.getDateNaissance());
		etdto.setTelephone(e.getTelephone());
		etdto.setEmail(e.getEmail());
		etdto.setMatricule(e.getMatricule());
		etdto.setAdresse(e.getAdresse());
		
		return etdto;
	}
}
