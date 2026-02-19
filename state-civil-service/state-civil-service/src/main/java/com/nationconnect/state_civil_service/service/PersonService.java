package com.nationconnect.state_civil_service.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nationconnect.state_civil_service.event.EventPublisher;
import com.nationconnect.state_civil_service.grpc.IdentityClient;
import com.nationconnect.state_civil_service.model.Person;
import com.nationconnect.state_civil_service.repository.PersonRepository;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final EventPublisher eventPublisher;
    private final IdentityClient identityClient;

    public PersonService(PersonRepository personRepository,
                         EventPublisher eventPublisher,
                         IdentityClient identityClient) {
        this.personRepository = personRepository;
        this.eventPublisher = eventPublisher;
        this.identityClient = identityClient;
    }

    // ===================== READ =====================

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Person findById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Personne non trouvée (id=" + id + ")"
                ));
    }

    // ===================== CREATE (gRPC + Retry) =====================

    /**
     * gRPC + Résilience:
     * - on vérifie le nationalId via IdentityClient (gRPC)
     * - en cas de problème réseau -> Retry 3 fois (2s)
     */
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public Person create(Person person) {

        // 1) Vérification doublon en base (409 Conflict)
        if (personRepository.existsByNationalId(person.getNationalId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce National ID est déjà enregistré."
            );
        }

        // 2) Vérification gRPC
        boolean isValid = identityClient.verify(person.getNationalId());

        if (!isValid) {
            // 400 car la donnée est invalide
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ÉCHEC gRPC : L'ID '" + person.getNationalId() + "' est invalide (doit avoir 10 chiffres)."
            );
        }

        // 3) Save PostgreSQL
        Person saved = personRepository.save(person);

        // 4) Event Kafka / EDA
        eventPublisher.publishPersonCreated(saved);

        return saved;
    }

    /**
     * Fallback: si après 3 tentatives gRPC ne répond pas.
     * On renvoie 503 Service Unavailable (service externe down)
     */
    @Recover
    public Person recover(Exception e, Person person) {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Le service d'identité est injoignable. Impossible de vérifier l'ID."
        );
    }

    // ===================== UPDATE =====================

    public Person update(Long id, Person updated) {
        Person existing = findById(id);

        // Si tu veux empêcher de changer nationalId, dis-moi et je te fais la règle
        // (sinon on vérifie l'unicité quand il change)

        if (updated.getNationalId() != null
                && !updated.getNationalId().equals(existing.getNationalId())
                && personRepository.existsByNationalId(updated.getNationalId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "National ID déjà utilisé par une autre personne."
            );
        }

        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setDateOfBirth(updated.getDateOfBirth());
        existing.setNationalId(updated.getNationalId());

        return personRepository.save(existing);
    }

    // ===================== DELETE =====================

    public void delete(Long id) {
        // pour avoir 404 si id n'existe pas
        Person existing = findById(id);
        personRepository.delete(existing);
    }
}
