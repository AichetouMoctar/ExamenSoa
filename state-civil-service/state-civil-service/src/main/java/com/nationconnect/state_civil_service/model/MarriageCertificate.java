package com.nationconnect.state_civil_service.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class MarriageCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String registryNumber;

    private LocalDate marriageDate;

    private String marriagePlace;

    @ManyToOne
    @JoinColumn(name = "husband_id")
    @JsonIgnoreProperties({"marriagesAsHusband", "marriagesAsWife"})
    private Person husband;

    @ManyToOne
    @JoinColumn(name = "wife_id")
    @JsonIgnoreProperties({"marriagesAsHusband", "marriagesAsWife"})
    private Person wife;

    @Enumerated(EnumType.STRING)
    private StatusMariage status;

    // Constructeur vide
    public MarriageCertificate() {}

    // Constructeur complet
    public MarriageCertificate(String registryNumber, LocalDate marriageDate, String marriagePlace,
                               Person husband, Person wife, StatusMariage status) {
        this.registryNumber = registryNumber;
        this.marriageDate = marriageDate;
        this.marriagePlace = marriagePlace;
        this.husband = husband;
        this.wife = wife;
        this.status = status;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRegistryNumber() { return registryNumber; }
    public void setRegistryNumber(String registryNumber) { this.registryNumber = registryNumber; }

    public LocalDate getMarriageDate() { return marriageDate; }
    public void setMarriageDate(LocalDate marriageDate) { this.marriageDate = marriageDate; }

    public String getMarriagePlace() { return marriagePlace; }
    public void setMarriagePlace(String marriagePlace) { this.marriagePlace = marriagePlace; }

    public Person getHusband() { return husband; }
    public void setHusband(Person husband) { this.husband = husband; }

    public Person getWife() { return wife; }
    public void setWife(Person wife) { this.wife = wife; }

    public StatusMariage getStatus() { return status; }
    public void setStatus(StatusMariage status) { this.status = status; }
}
