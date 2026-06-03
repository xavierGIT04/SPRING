package com.iitL32025.guniv.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.iitL32025.guniv.models.Moto;
import com.iitL32025.guniv.repository.MotoRepository;

@Service
public class MotoServiceImpl implements MotoService{
	
	private final MotoRepository mr;
	

	public MotoServiceImpl(MotoRepository mr) {
		super();
		this.mr = mr;
	}

	@Override
	public List<Moto> getMotos() {
		return mr.findAll();
	}

	@Override
	public Moto add(Moto m) {
		return mr.save(m);
	}

}
