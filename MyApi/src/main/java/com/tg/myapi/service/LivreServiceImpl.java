package com.tg.myapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tg.myapi.models.Livre;
import com.tg.myapi.repository.LivreRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class LivreServiceImpl implements LivreService{
	
	private final LivreRepository lr;
	
	LivreServiceImpl(LivreRepository r){
		this.lr = r;
	}

	@Override
	public Livre add(Livre l) {
		
		return lr.save(l);
	}

	@Override
	public List<Livre> getLivre() {
		return lr.findAll();
	}

	@Override
	public Livre updateLivre(Livre l) {
		Livre lastLivre = lr.findById(l.getId()).orElseThrow();
		lastLivre.setAuteur(l.getAuteur());
		lastLivre.setTitre(l.getTitre());
		lastLivre.setIsbn(l.getIsbn());
		lastLivre.setNombreExemplaire(l.getNombrExemplaire());
		lastLivre.setNombreExemplaireDispo(l.getNombrExemplaireDispo());
	
		return lr.save(lastLivre);
	}

	@Override
	public void deleteLivre(Long id) {
		Livre livre = lr.findById(id).orElseThrow();
		lr.delete(livre);
		
	}

}
