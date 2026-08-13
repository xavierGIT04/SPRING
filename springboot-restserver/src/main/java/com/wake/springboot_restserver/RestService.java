package com.wake.springboot_restserver;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class RestService {

	public static final org.slf4j.Logger logger = LoggerFactory.getLogger(RestService.class); 
	
	@GetMapping("/")
	public ResponseEntity<String> pong() {
		logger.info("Démarrage effectif des services rest");
		return new ResponseEntity<String>("Serveur démarré avec le statut " +HttpStatus.OK.name() , HttpStatus.OK);
	}
	
}
