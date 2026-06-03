package com.wake.GreenBank.service;

import com.wake.GreenBank.dto.request.ClientDtoRequest;
import com.wake.GreenBank.dto.response.ClientDtoResponse;

public interface ClientService {
	public ClientDtoResponse create(ClientDtoRequest c);
}
