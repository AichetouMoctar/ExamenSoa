package com.nationconnect.state_civil_service.dto;

import java.time.LocalDate;

public record PersonDTO(
    String nationalId,
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String fullName
) {}