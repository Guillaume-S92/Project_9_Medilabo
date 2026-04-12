package com.medilabo.assessment.dto;

public record NoteClientResponse(
        String id,
        String patientId,
        String content,
        String practitionerUsername,
        String createdAt,
        String updatedAt
) {
}