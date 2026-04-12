package com.medilabo.assessment.dto;

import java.time.LocalDate;

public record PatientClientResponse(
        String id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String gender,
        String address,
        String phone
) {
}
