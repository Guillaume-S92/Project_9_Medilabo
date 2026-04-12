package com.medilabo.note.dto;

import java.time.Instant;

public record NoteResponse(
        String id,
        String patientId,
        String content,
        String practitionerUsername,
        Instant createdAt,
        Instant updatedAt
) {
}
