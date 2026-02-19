package com.nationconnect.state_civil_service.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(
    name = "person",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "nationalId")
    }
)
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;

    @Column(nullable = false, unique = true)
    private String nationalId;

    // Relations inverses pour MarriageCertificate
    @OneToMany(mappedBy = "husband")
    @JsonIgnoreProperties({"husband", "wife"})
    private List<MarriageCertificate> marriagesAsHusband;

    @OneToMany(mappedBy = "wife")
    @JsonIgnoreProperties({"husband", "wife"})
    private List<MarriageCertificate> marriagesAsWife;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public List<MarriageCertificate> getMarriagesAsHusband() { return marriagesAsHusband; }
    public void setMarriagesAsHusband(List<MarriageCertificate> marriagesAsHusband) {
        this.marriagesAsHusband = marriagesAsHusband;
    }

    public List<MarriageCertificate> getMarriagesAsWife() { return marriagesAsWife; }
    public void setMarriagesAsWife(List<MarriageCertificate> marriagesAsWife) {
        this.marriagesAsWife = marriagesAsWife;
    }

    // méthode pratique
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
