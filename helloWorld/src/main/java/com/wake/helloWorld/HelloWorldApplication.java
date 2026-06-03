package com.wake.helloWorld;
import com.wake.helloWorld.model.HelloWorld;
import com.wake.helloWorld.service.BusinessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelloWorldApplication implements CommandLineRunner{
	
	@Autowired
	private BusinessService bs;
	
	public static void main(String[] args) {
		SpringApplication.run(HelloWorldApplication.class, args);
	}
	

	@Override
	public void run(String... args) throws Exception {
		HelloWorld hw = bs.getHelloWorld();
		System.out.println(hw);
		
	}
}
