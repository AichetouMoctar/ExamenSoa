package com.nationconnect.state_civil_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nationconnect.state_civil_service.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

    boolean existsByNationalId(String nationalId);
}
