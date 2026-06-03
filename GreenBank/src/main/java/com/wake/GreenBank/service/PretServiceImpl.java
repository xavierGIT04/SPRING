package com.wake.GreenBank.service;

import org.springframework.stereotype.Service;

import com.wake.GreenBank.Mapper.PretMapper;
import com.wake.GreenBank.dto.request.PretDtoRequest;
import com.wake.GreenBank.dto.response.PretDtoResponse;
import com.wake.GreenBank.entity.Client;
import com.wake.GreenBank.enums.Statut;

import com.wake.GreenBank.entity.Pret;
import com.wake.GreenBank.repository.ClientRepository;
import com.wake.GreenBank.repository.PretRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PretServiceImpl implements PretServcie{
	
	private final PretRepository prep;
	private final ClientRepository clrep;
	
	

	public PretServiceImpl(PretRepository prep, ClientRepository clrep) {
		super();
		this.prep = prep;
		this.clrep = clrep;
	}



	@Override
	public PretDtoResponse demanderPret(PretDtoRequest p) {
		PretMapper pm = new PretMapper();
		Client client = clrep.findByPublicId(p.client()).orElseThrow(()-> new RuntimeException("Client not found"));
		Pret pret = new Pret();
		double M = p.montant()*1.05*p.dureeMois();
		
		if( M>(client.getRevenuAnnuel()/12)*0.3) {
			p.statut = Statut.REJETE;
			
		}
		pret = pm.dtoToPret(p);
		pret.setClient(client);
		

		return pm.pretToDto(prep.save(pret));
	}

}
