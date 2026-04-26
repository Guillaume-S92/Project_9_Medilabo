package com.medilabo.patient.service;

import com.medilabo.patient.dto.PatientRequest;
import com.medilabo.patient.dto.PatientResponse;
import com.medilabo.patient.entity.PatientEntity;
import com.medilabo.patient.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponse> findAll() {
        return patientRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public PatientResponse findById(String id) {
        PatientEntity entity = patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient introuvable"));
        return toResponse(entity);
    }

    public PatientResponse create(PatientRequest request) {
        Instant now = Instant.now();

        PatientEntity entity = new PatientEntity()
                .setId(UUID.randomUUID().toString())
                .setFirstName(request.firstName())
                .setLastName(request.lastName())
                .setBirthDate(request.birthDate())
                .setGender(request.gender())
                .setAddress(request.address())
                .setPhone(request.phone())
                .setCreatedAt(now)
                .setUpdatedAt(now);

        return toResponse(patientRepository.save(entity));
    }

    public PatientResponse update(String id, PatientRequest request) {
        PatientEntity existing = patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient introuvable"));

        existing.setFirstName(request.firstName())
                .setLastName(request.lastName())
                .setBirthDate(request.birthDate())
                .setGender(request.gender())
                .setAddress(request.address())
                .setPhone(request.phone())
                .setUpdatedAt(Instant.now());

        return toResponse(patientRepository.save(existing));
    }

    public void delete(String id) {
        if (!patientRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient introuvable");
        }
        patientRepository.deleteById(id);
    }

    private PatientResponse toResponse(PatientEntity entity) {
        return new PatientResponse(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBirthDate(),
                entity.getGender(),
                entity.getAddress(),
                entity.getPhone(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}