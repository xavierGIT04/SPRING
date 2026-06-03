package com.iitL32025.guniv.service;

import java.util.List;

import com.iitL32025.guniv.models.Moto;

public interface MotoService {
	List<Moto> getMotos();
	Moto add(Moto m);
}
