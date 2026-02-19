package com.nationconnect.state_civil_service.dto;

import java.time.LocalDate;

public record BirthCertificateDTO(
    String registryNumber,
    LocalDate dateOfBirth,
    String placeOfBirth,
    String status,
    String childNationalId,
    String childFullName,
    String fatherFullName,
    String motherFullName
) {}