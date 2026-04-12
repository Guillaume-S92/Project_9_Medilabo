package com.medilabo.note.document;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("patient_notes")
public class PatientNoteDocument {

    @Id
    private String id;

    @Indexed
    private String patientId;

    private String content;
    private String practitionerUsername;
    private Instant createdAt;
    private Instant updatedAt;

    public String getId() { return id; }
    public PatientNoteDocument setId(String id) { this.id = id; return this; }
    public String getPatientId() { return patientId; }
    public PatientNoteDocument setPatientId(String patientId) { this.patientId = patientId; return this; }
    public String getContent() { return content; }
    public PatientNoteDocument setContent(String content) { this.content = content; return this; }
    public String getPractitionerUsername() { return practitionerUsername; }
    public PatientNoteDocument setPractitionerUsername(String practitionerUsername) { this.practitionerUsername = practitionerUsername; return this; }
    public Instant getCreatedAt() { return createdAt; }
    public PatientNoteDocument setCreatedAt(Instant createdAt) { this.createdAt = createdAt; return this; }
    public Instant getUpdatedAt() { return updatedAt; }
    public PatientNoteDocument setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
}
