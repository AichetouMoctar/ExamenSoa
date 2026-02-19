package com.nationconnect.state_civil_service.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PersonListener {

    @EventListener
    public void handlePersonCreated(PersonCreatedEvent event) {
        System.out.println(
            " EVENT: New person created → " +
            event.getPerson().getFullName() +
            " | NationalId = " +
            event.getPerson().getNationalId()
        );
    }
}
