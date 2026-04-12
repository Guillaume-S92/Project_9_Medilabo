package com.medilabo.patient.dto;

import java.time.LocalDate;

import com.medilabo.patient.entity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public record PatientRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull @Past LocalDate birthDate,
        @NotNull Gender gender,
        String address,
        String phone
) {
}
