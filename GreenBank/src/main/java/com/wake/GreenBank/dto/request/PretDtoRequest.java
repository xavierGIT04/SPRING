package com.wake.GreenBank.dto.request;


import java.util.UUID;

import com.wake.GreenBank.enums.Statut;


public record PretDtoRequest(
		Float montant, int dureeMois, Statut statut, UUID client) {

}
