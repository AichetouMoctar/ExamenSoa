package com.nationconnect.state_civil_service.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "birth_certificate")
public class BirthCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String registryNumber;
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String status;

    // الربط مباشرة مع Person
    @ManyToOne(optional = false)
    @JoinColumn(name = "child_id")
    @JsonIgnoreProperties({"marriagesAsHusband", "marriagesAsWife"})
    private Person child;

    @ManyToOne(optional = false)
    @JoinColumn(name = "father_id")
    @JsonIgnoreProperties({"marriagesAsHusband", "marriagesAsWife"})
    private Person father;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mother_id")
    @JsonIgnoreProperties({"marriagesAsHusband", "marriagesAsWife"})
    private Person mother;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRegistryNumber() { return registryNumber; }
    public void setRegistryNumber(String registryNumber) { this.registryNumber = registryNumber; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPlaceOfBirth() { return placeOfBirth; }
    public void setPlaceOfBirth(String placeOfBirth) { this.placeOfBirth = placeOfBirth; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Person getChild() { return child; }
    public void setChild(Person child) { this.child = child; }

    public Person getFather() { return father; }
    public void setFather(Person father) { this.father = father; }

    public Person getMother() { return mother; }
    public void setMother(Person mother) { this.mother = mother; }
}
