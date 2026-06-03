package com.wake.GreenBank.Mapper;

import com.wake.GreenBank.*;
import com.wake.GreenBank.dto.request.ClientDtoRequest;
import com.wake.GreenBank.dto.response.ClientDtoResponse;
import com.wake.GreenBank.entity.Client;


public class ClientMapper {
	
	public Client dtoToClient(ClientDtoRequest cl) {
		Client client = new Client();
		client.setNom(cl.nom());
		client.setEmail(cl.email());
		client.setRevenuAnnuel(cl.revenuAnnuel());
		return client;
	}
	
	public ClientDtoResponse clientToDto(Client client) {
		ClientDtoResponse response = 
				new ClientDtoResponse
				(
					client.getPublicId(),
					client.getNom(),
					client.getEmail(),
					client.getRevenuAnnuel()
				);
		return response;
	}

}
