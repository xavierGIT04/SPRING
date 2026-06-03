package tg.ipnet.FirstSpring.Mappers;

import tg.ipnet.FirstSpring.DTO.Request.FiliereDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.FiliereDtoResponse;
import tg.ipnet.FirstSpring.entity.Filiere;

public class FiliereMappers {
	
	public FiliereDtoResponse toDto(Filiere f) {
		FiliereDtoResponse fdto = new FiliereDtoResponse();
		fdto.setId(f.getId());
		fdto.setNomFiliere(f.getNomFiliere());
		return fdto;
	}
	
	public Filiere toFiliere(FiliereDtoRequest fdto) {
		Filiere f = new Filiere();
		f.setNomFiliere(fdto.getNomFiliere());
		return f;
	}
}
