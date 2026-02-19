package com.nationconnect.state_civil_service.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nationconnect.state_civil_service.model.BirthCertificate;
import com.nationconnect.state_civil_service.service.BirthService;

@RestController
@RequestMapping("/api/birth-certificates")
public class BirthController {

    private final BirthService service;

    public BirthController(BirthService service) {
        this.service = service;
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<List<BirthCertificate>> getAll() {
        // Vérifie que la méthode s'appelle bien getAllBirthCertificates dans ton BirthService
        return ResponseEntity.ok(service.getAllBirthCertificates());
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<BirthCertificate> getById(@PathVariable Long id) {
        // Cette méthode doit exister dans BirthService
        BirthCertificate bc = service.getBirthCertificateById(id);
        return ResponseEntity.ok(bc);
    }

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<?> create(@RequestBody BirthCertificate birthCertificate) {
        try {
            BirthCertificate saved = service.createBirthCertificate(birthCertificate);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            // Important : On capture l'erreur gRPC ou métier pour l'afficher proprement
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<BirthCertificate> update(
            @PathVariable Long id,
            @RequestBody BirthCertificate birthCertificate) {
        BirthCertificate updated = service.updateBirthCertificate(id, birthCertificate);
        return ResponseEntity.ok(updated);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.deleteBirthCertificate(id);
        return ResponseEntity.ok("Birth certificate deleted successfully");
    }
}