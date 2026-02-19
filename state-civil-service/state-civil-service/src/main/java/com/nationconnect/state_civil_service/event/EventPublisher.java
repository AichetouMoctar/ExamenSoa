package com.nationconnect.state_civil_service.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import com.nationconnect.state_civil_service.model.*;

@Component
public class EventPublisher {

    private final ApplicationEventPublisher publisher;

    public EventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishPersonCreated(Person person) {
        System.out.println("📢 [EVENT] Nouvelle Personne créée : " + person.getFirstName() + " (ID: " + person.getNationalId() + ")");
        publisher.publishEvent(person);
    }

    public void publishBirth(BirthCertificate birth) {
        System.out.println("📢 [EVENT] Nouvel Acte de Naissance N° : " + birth.getRegistryNumber());
        publisher.publishEvent(birth);
    }

    public void publishMarriage(MarriageCertificate marriage) {
        System.out.println("📢 [EVENT] Nouveau Mariage enregistré entre : " + 
            marriage.getHusband().getLastName() + " et " + marriage.getWife().getLastName());
        publisher.publishEvent(marriage);
    }
}