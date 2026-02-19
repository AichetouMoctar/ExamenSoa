package com.nationconnect.state_civil_service;

import org.springframework.ai.autoconfigure.retry.SpringAiRetryAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
// On ajoute l'exclusion ici pour ignorer le module qui fait planter le projet
@SpringBootApplication(exclude = {SpringAiRetryAutoConfiguration.class})
public class StateCivilServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StateCivilServiceApplication.class, args);
    }

}