package com.nationconnect.state_civil_service.event;

import com.nationconnect.state_civil_service.model.MarriageCertificate;

public class MarriageCreatedEvent {

    private final MarriageCertificate marriage;

    public MarriageCreatedEvent(MarriageCertificate marriage) {
        this.marriage = marriage;
    }

    public MarriageCertificate getMarriage() {
        return marriage;
    }
}
