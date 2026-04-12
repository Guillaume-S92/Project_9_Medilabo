package com.medilabo.patient.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.medilabo.patient.entity.Gender;

public record PatientResponse(
        String id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        Gender gender,
        String address,
        String phone,
        Instant createdAt,
        Instant updatedAt
) {
}
