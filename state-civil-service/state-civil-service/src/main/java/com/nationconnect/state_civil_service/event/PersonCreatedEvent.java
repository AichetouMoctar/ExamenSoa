package com.nationconnect.state_civil_service.event;

import com.nationconnect.state_civil_service.model.Person;

public class PersonCreatedEvent {

    private final Person person;

    public PersonCreatedEvent(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }
}
