package com.medilabo.note.service;

import java.time.Instant;
import java.util.List;

import com.medilabo.note.document.PatientNoteDocument;
import com.medilabo.note.dto.NoteRequest;
import com.medilabo.note.dto.NoteResponse;
import com.medilabo.note.repository.PatientNoteRepository;
import org.springframework.stereotype.Service;

@Service
public class PatientNoteService {

    private final PatientNoteRepository patientNoteRepository;

    public PatientNoteService(PatientNoteRepository patientNoteRepository) {
        this.patientNoteRepository = patientNoteRepository;
    }

    public List<NoteResponse> findAll() {
        return patientNoteRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toResponse)
                .toList();
    }

    public List<NoteResponse> findByPatientId(String patientId) {
        return patientNoteRepository.findByPatientIdOrderByCreatedAtDesc(patientId).stream()
                .map(this::toResponse)
                .toList();
    }

    public NoteResponse create(NoteRequest request, String practitionerUsername) {
        Instant now = Instant.now();
        PatientNoteDocument document = new PatientNoteDocument()
                .setPatientId(request.patientId())
                .setContent(request.content())
                .setPractitionerUsername(practitionerUsername)
                .setCreatedAt(now)
                .setUpdatedAt(now);
        return toResponse(patientNoteRepository.save(document));
    }

    private NoteResponse toResponse(PatientNoteDocument document) {
        return new NoteResponse(document.getId(), document.getPatientId(), document.getContent(),
                document.getPractitionerUsername(), document.getCreatedAt(), document.getUpdatedAt());
    }
}
