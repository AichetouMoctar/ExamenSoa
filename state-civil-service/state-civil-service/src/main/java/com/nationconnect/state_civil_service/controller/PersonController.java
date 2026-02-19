package com.nationconnect.state_civil_service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nationconnect.state_civil_service.model.Person;
import com.nationconnect.state_civil_service.service.PersonService;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    // 1) GET ALL
    @GetMapping
    public ResponseEntity<List<Person>> getAllPersons() {
        return ResponseEntity.ok(personService.findAll());
    }

    // 2) GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        return ResponseEntity.ok(personService.findById(id));
    }

    // 3) CREATE
    @PostMapping
    public ResponseEntity<Map<String, String>> createPerson(@RequestBody Person person) {

        Person saved = personService.create(person); // ✅ retourne Person

        Map<String, String> response = new HashMap<>();
        response.put("message", "SUCCÈS : Personne créée et enregistrée en base de données.");
        response.put("id", String.valueOf(saved.getId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 4) UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(
            @PathVariable Long id,
            @RequestBody Person person) {

        Person updated = personService.update(id, person);
        return ResponseEntity.ok(updated);
    }

    // 5) DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
