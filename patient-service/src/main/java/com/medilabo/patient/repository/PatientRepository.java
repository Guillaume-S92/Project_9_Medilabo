package com.medilabo.patient.repository;

import com.medilabo.patient.document.PatientDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatientRepository extends MongoRepository<PatientDocument, String> {
}
