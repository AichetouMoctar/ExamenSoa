package com.nationconnect.state_civil_service.dto;

import java.time.LocalDate;

public record MarriageCertificateDTO(
    String registryNumber,
    LocalDate marriageDate,
    String marriagePlace,
    String husbandFullName,
    String wifeFullName,
    String status
) {}