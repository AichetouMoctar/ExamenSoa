package com.nationconnect.state_civil_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
@EnableRetry
@SpringBootApplication
public class StateCivilServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StateCivilServiceApplication.class, args);
	}

}
