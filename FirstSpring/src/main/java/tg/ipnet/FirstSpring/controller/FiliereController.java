package tg.ipnet.FirstSpring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import tg.ipnet.FirstSpring.DTO.Request.FiliereDtoRequest;
import tg.ipnet.FirstSpring.DTO.Response.FiliereDtoResponse;
import tg.ipnet.FirstSpring.service.FiliereService;
import tg.ipnet.FirstSpring.utils.BaseResponse;

@RestController
@RequestMapping("api/filiere/")
public class FiliereController {
	
	@Autowired
	FiliereService filiereService;
	
	@GetMapping("/")
	public BaseResponse<List<FiliereDtoResponse>> filieres(){
		BaseResponse<List<FiliereDtoResponse>> response = new BaseResponse<>();
		response.setData(filiereService.list());
		response.setStatus(200);
		response.setDescription("Liste des filières récupérer avec succès");
		return response;
	}
	
	@GetMapping("/{id}/")
	public BaseResponse<FiliereDtoResponse>  getFiliere(@PathVariable Long id) {
		BaseResponse<FiliereDtoResponse> response = new BaseResponse<>();
		response.setData( filiereService.getFiliere(id));
		response.setStatus(200);
		response.setDescription("Filière récupérer avec succès");
		return response;
		
	}
	
	@PostMapping("/")
	public BaseResponse<FiliereDtoResponse>  addFiliere(@RequestBody FiliereDtoRequest fdto) {
		BaseResponse<FiliereDtoResponse> response = new BaseResponse<>();
		response.setData(filiereService.create(fdto));
		response.setStatus(200);
		response.setDescription("Filière créée avec succès");
		return response;
	}
	
	@PutMapping("/{id}/")
	public BaseResponse<FiliereDtoResponse>  updateFiliere(@RequestBody FiliereDtoRequest fdto, @PathVariable Long id) {
		BaseResponse<FiliereDtoResponse> response = new BaseResponse<>();
		response.setData(filiereService.update(fdto, id));
		response.setStatus(200);
		response.setDescription("Filière modifiée avec succès");
		return response;
	}
	
	@DeleteMapping("/{id}/")
	public void deleteFiliere(@PathVariable Long id) {
		filiereService.delete(id);
	}
	
	
	
}
