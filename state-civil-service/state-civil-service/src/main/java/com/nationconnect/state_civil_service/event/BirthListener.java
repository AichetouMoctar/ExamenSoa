package com.nationconnect.state_civil_service.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BirthListener {

    @EventListener
    public void handleBirth(BirthCreatedEvent event) {
        System.out.println("📢 New birth registered: " + event.registryNumber());
    }
}
