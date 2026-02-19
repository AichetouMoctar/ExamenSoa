package com.nationconnect.state_civil_service.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MarriageListener {

    @EventListener
    public void handleMarriageCreated(MarriageCreatedEvent event) {
        System.out.println(
            "📢 Marriage Event received: registry = "
            + event.getMarriage().getRegistryNumber()
        );
    }
}
