package com.medilabo.note.dto;

import jakarta.validation.constraints.NotBlank;

public record NoteRequest(
        @NotBlank String patientId,
        @NotBlank String content
) {
}
