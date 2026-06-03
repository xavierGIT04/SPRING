package com.wake.GreenBank.service;

import com.wake.GreenBank.dto.request.PretDtoRequest;
import com.wake.GreenBank.dto.response.PretDtoResponse;

public interface PretServcie {
	public PretDtoResponse demanderPret(PretDtoRequest p);
}
