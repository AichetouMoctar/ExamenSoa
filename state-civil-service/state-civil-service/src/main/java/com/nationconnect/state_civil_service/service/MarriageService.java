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
import com.nationconnect.state_civil_service.model.MarriageCertificate;
import com.nationconnect.state_civil_service.model.Person;
import com.nationconnect.state_civil_service.repository.MarriageRepository;
import com.nationconnect.state_civil_service.repository.PersonRepository;

@Service
public class MarriageService {

    private static final Logger log = LoggerFactory.getLogger(MarriageService.class);

    private final MarriageRepository marriageRepo;
    private final PersonRepository personRepository;
    private final EventPublisher eventPublisher;
    private final IdentityClient identityClient;

    public MarriageService(MarriageRepository marriageRepo,
                           PersonRepository personRepository,
                           EventPublisher eventPublisher,
                           IdentityClient identityClient) {
        this.marriageRepo = marriageRepo;
        this.personRepository = personRepository;
        this.eventPublisher = eventPublisher;
        this.identityClient = identityClient;
    }

    @Retryable(
            retryFor = { io.grpc.StatusRuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public MarriageCertificate register(MarriageCertificate marriage) {
        log.info("Enregistrement d'un nouveau mariage...");

        if (marriage.getHusband() == null || marriage.getWife() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID du mari et de la femme manquants.");
        }

        // 1. Chargement des entités depuis la DB
        Person husband = personRepository.findById(marriage.getHusband().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mari non trouvé en base."));
        
        Person wife = personRepository.findById(marriage.getWife().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Femme non trouvée en base."));

        // 2. Appel gRPC
        log.info("Appel gRPC pour Mari: {} | Femme: {}", husband.getNationalId(), wife.getNationalId());
        
        boolean isHusbandValid = identityClient.verify(husband.getNationalId());
        boolean isWifeValid = identityClient.verify(wife.getNationalId());

        // Log précis pour savoir qui est invalide dans le Mock
        if (!isHusbandValid) log.error("Le service d'identité a rejeté le Mari: {}", husband.getNationalId());
        if (!isWifeValid) log.error("Le service d'identité a rejeté la Femme: {}", wife.getNationalId());

        if (!isHusbandValid || !isWifeValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation d'identité échouée par le service gRPC.");
        }

        // 3. Finalisation
        marriage.setHusband(husband);
        marriage.setWife(wife);

        MarriageCertificate saved = marriageRepo.save(marriage);
        eventPublisher.publishMarriage(saved);
        
        log.info("Mariage enregistré avec succès ! ID: {}", saved.getId());
        return saved;
    }

    public List<MarriageCertificate> findAll() {
        return marriageRepo.findAll();
    }

    public MarriageCertificate findById(Long id) {
        return marriageRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mariage non trouvé."));
    }

    public MarriageCertificate update(Long id, MarriageCertificate updated) {
        MarriageCertificate existing = findById(id);
        existing.setMarriageDate(updated.getMarriageDate());
        existing.setHusband(updated.getHusband());
        existing.setWife(updated.getWife());
        return marriageRepo.save(existing);
    }

    public void delete(Long id) {
        MarriageCertificate existing = findById(id);
        marriageRepo.delete(existing);
    }

    @Recover
    public MarriageCertificate recover(io.grpc.StatusRuntimeException e, MarriageCertificate marriage) {
        log.error("ERREUR CRITIQUE: Service gRPC injoignable après 3 tentatives.");
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Service d'identité indisponible.");
    }
}