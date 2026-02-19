package com.nationconnect.state_civil_service.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nationconnect.state_civil_service.event.EventPublisher;
import com.nationconnect.state_civil_service.grpc.IdentityClient;
import com.nationconnect.state_civil_service.model.BirthCertificate;
import com.nationconnect.state_civil_service.model.Person;
import com.nationconnect.state_civil_service.repository.BirthRepository;
import com.nationconnect.state_civil_service.repository.PersonRepository;

@Service
public class BirthService {

    private static final Logger log = LoggerFactory.getLogger(BirthService.class);

    private final BirthRepository repository;
    private final PersonRepository personRepository;
    private final EventPublisher eventPublisher;
    private final IdentityClient identityClient;

    public BirthService(BirthRepository repository,
                        PersonRepository personRepository,
                        EventPublisher eventPublisher,
                        IdentityClient identityClient) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.eventPublisher = eventPublisher;
        this.identityClient = identityClient;
    }

    // ✅ Nom correspondant au contrôleur : service.getAllBirthCertificates()
    public List<BirthCertificate> getAllBirthCertificates() {
        return repository.findAll();
    }

    // ✅ Nom correspondant au contrôleur : service.getBirthCertificateById(id)
    public BirthCertificate getBirthCertificateById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Acte de naissance non trouvé (id=" + id + ")"));
    }

    // ✅ Nom correspondant au contrôleur : service.createBirthCertificate(bc)
    @Retryable(
            retryFor = { io.grpc.StatusRuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public BirthCertificate createBirthCertificate(BirthCertificate birthCertificate) {
        log.info("Création d'un acte de naissance via gRPC...");

        // Récupération des parents
        Person father = personRepository.findById(birthCertificate.getFather().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Père non trouvé."));
        Person mother = personRepository.findById(birthCertificate.getMother().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mère non trouvée."));
        Person child = personRepository.findById(birthCertificate.getChild().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enfant non trouvé."));

        // Vérification gRPC
        boolean fatherValid = identityClient.verify(father.getNationalId());
        boolean motherValid = identityClient.verify(mother.getNationalId());

        if (!fatherValid || !motherValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'identité des parents est invalide.");
        }

        birthCertificate.setFather(father);
        birthCertificate.setMother(mother);
        birthCertificate.setChild(child);

        BirthCertificate saved = repository.save(birthCertificate);
        eventPublisher.publishBirth(saved);
        return saved;
    }

    // ✅ Nom correspondant au contrôleur : service.updateBirthCertificate(id, bc)
    public BirthCertificate updateBirthCertificate(Long id, BirthCertificate updated) {
        BirthCertificate existing = getBirthCertificateById(id);
        existing.setRegistryNumber(updated.getRegistryNumber());
        existing.setDateOfBirth(updated.getDateOfBirth());
        existing.setPlaceOfBirth(updated.getPlaceOfBirth());
        existing.setStatus(updated.getStatus());
        return repository.save(existing);
    }

    // ✅ Nom correspondant au contrôleur : service.deleteBirthCertificate(id)
    public void deleteBirthCertificate(Long id) {
        BirthCertificate existing = getBirthCertificateById(id);
        repository.delete(existing);
    }

    // Fallback pour gRPC
    @Recover
    public BirthCertificate recover(io.grpc.StatusRuntimeException e, BirthCertificate bc) {
        log.error("Service d'identité indisponible.");
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Vérification d'identité impossible (503).");
    }
}