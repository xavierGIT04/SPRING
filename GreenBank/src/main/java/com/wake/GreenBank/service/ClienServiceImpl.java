package com.wake.GreenBank.service;

import com.wake.GreenBank.dto.request.ClientDtoRequest;
import com.wake.GreenBank.dto.response.ClientDtoResponse;
import com.wake.GreenBank.repository.ClientRepository;

public class ClienServiceImpl implements ClientService{
	
	private final ClientRepository clrep;
	
	

	public ClienServiceImpl(ClientRepository clrep) {
		super();
		this.clrep = clrep;
	}



	@Override
	public ClientDtoResponse create(ClientDtoRequest c) {
		// TODO Auto-generated method stub
		return null;
	}

}
