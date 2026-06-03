package com.wake.helloWorld.service;

import org.springframework.stereotype.Component;

import com.wake.helloWorld.model.HelloWorld;

@Component
public class BusinessService {
	
	public HelloWorld getHelloWorld() {
		HelloWorld hw = new HelloWorld();
		return hw ;
	}

}
