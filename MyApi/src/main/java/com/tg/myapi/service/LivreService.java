package com.tg.myapi.service;

import java.util.List;

import com.tg.myapi.models.Livre;

public interface LivreService {
	
	Livre add(Livre l);
	List<Livre> getLivre();
	Livre updateLivre(Livre l);
	void deleteLivre(Long id);

}
