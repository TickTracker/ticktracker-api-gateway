package com.ticktracker.gatewayservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TicktrackerApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicktrackerApiGatewayApplication.class, args);
	}

	@Bean
	public ObjectMapper getObjectMapper()
	{
		return new ObjectMapper();
	}

}
