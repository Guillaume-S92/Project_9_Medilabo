package com.medilabo.note.repository;

import java.util.List;

import com.medilabo.note.document.PatientNoteDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatientNoteRepository extends MongoRepository<PatientNoteDocument, String> {
    List<PatientNoteDocument> findByPatientIdOrderByCreatedAtDesc(String patientId);
}
