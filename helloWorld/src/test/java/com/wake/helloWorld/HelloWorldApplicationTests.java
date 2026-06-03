package com.wake.helloWorld;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.wake.helloWorld.service.BusinessService;

@SpringBootTest
class HelloWorldApplicationTests {
	
	@Autowired
	private BusinessService bs;
	
	@Test
	void contextLoads() {
	}
	
	@Test
	public void testGetHelloWorld() {
		String attendu = "Hello World !";
		String resultat = bs.getHelloWorld().getValue();
		assertEquals(attendu, resultat);
	}

}
