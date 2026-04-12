package com.medilabo.patient.service;

import java.time.Instant;
import java.util.List;

import com.medilabo.patient.document.PatientDocument;
import com.medilabo.patient.dto.PatientRequest;
import com.medilabo.patient.dto.PatientResponse;
import com.medilabo.patient.repository.PatientRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;
    private final MongoTemplate mongoTemplate;

    public PatientService(PatientRepository patientRepository, MongoTemplate mongoTemplate) {
        this.patientRepository = patientRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<PatientResponse> findAll() {
        return patientRepository.findAll().stream().map(this::toResponse).toList();
    }

    public PatientResponse findById(String id) {
        String cleanId = id == null ? null : id.trim();
        log.info("Recherche patient par id brut='{}' / nettoye='{}'", id, cleanId);

        ObjectId objectId = toObjectId(cleanId);
        PatientDocument document = mongoTemplate.findById(objectId, PatientDocument.class);

        if (document == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient introuvable");
        }

        return toResponse(document);
    }

    public PatientResponse create(PatientRequest request) {
        Instant now = Instant.now();
        PatientDocument document = new PatientDocument()
                .setFirstName(request.firstName())
                .setLastName(request.lastName())
                .setBirthDate(request.birthDate())
                .setGender(request.gender())
                .setAddress(request.address())
                .setPhone(request.phone())
                .setCreatedAt(now)
                .setUpdatedAt(now);
        return toResponse(patientRepository.save(document));
    }

    public PatientResponse update(String id, PatientRequest request) {
        String cleanId = id == null ? null : id.trim();
        log.info("Mise a jour patient par id brut='{}' / nettoye='{}'", id, cleanId);

        ObjectId objectId = toObjectId(cleanId);
        PatientDocument existing = mongoTemplate.findById(objectId, PatientDocument.class);

        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient introuvable");
        }

        existing.setFirstName(request.firstName())
                .setLastName(request.lastName())
                .setBirthDate(request.birthDate())
                .setGender(request.gender())
                .setAddress(request.address())
                .setPhone(request.phone())
                .setUpdatedAt(Instant.now());

        return toResponse(patientRepository.save(existing));
    }

    private ObjectId toObjectId(String id) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifiant patient invalide");
        }

        try {
            return new ObjectId(id);
        } catch (IllegalArgumentException ex) {
            log.error("Identifiant patient invalide recu: '{}'", id, ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifiant patient invalide");
        }
    }

    private PatientResponse toResponse(PatientDocument document) {
        return new PatientResponse(
                document.getId(),
                document.getFirstName(),
                document.getLastName(),
                document.getBirthDate(),
                document.getGender(),
                document.getAddress(),
                document.getPhone(),
                document.getCreatedAt(),
                document.getUpdatedAt());
    }
}