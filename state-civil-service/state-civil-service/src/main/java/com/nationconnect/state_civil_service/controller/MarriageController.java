package com.nationconnect.state_civil_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nationconnect.state_civil_service.model.MarriageCertificate;
import com.nationconnect.state_civil_service.service.MarriageService;

@RestController
@RequestMapping("/api/marriages")
public class MarriageController {

    private final MarriageService service;

    public MarriageController(MarriageService service) {
        this.service = service;
    }

    // 🔹 CREATE
    @PostMapping
    public ResponseEntity<MarriageCertificate> create(
            @RequestBody MarriageCertificate marriage) {

        MarriageCertificate saved = service.register(marriage);
        return ResponseEntity.status(201).body(saved); // 201 Created
    }

    // 🔹 GET ALL
    @GetMapping
    public ResponseEntity<List<MarriageCertificate>> all() {
        return ResponseEntity.ok(service.findAll()); // 200 OK
    }

    // 🔹 GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<MarriageCertificate> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // 🔹 UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<MarriageCertificate> update(
            @PathVariable Long id,
            @RequestBody MarriageCertificate updated) {

        return ResponseEntity.ok(service.update(id, updated));
    }

    // 🔹 DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}