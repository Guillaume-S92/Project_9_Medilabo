package com.medilabo.note.controller;

import java.util.List;

import com.medilabo.note.dto.NoteRequest;
import com.medilabo.note.dto.NoteResponse;
import com.medilabo.note.service.PatientNoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final PatientNoteService patientNoteService;

    public NoteController(PatientNoteService patientNoteService) {
        this.patientNoteService = patientNoteService;
    }

    @GetMapping
    @PreAuthorize("hasRole('PRACTITIONER')")
    public List<NoteResponse> findAll() {
        return patientNoteService.findAll();
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('PRACTITIONER')")
    public List<NoteResponse> findByPatientId(@PathVariable String patientId) {
        return patientNoteService.findByPatientId(patientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PRACTITIONER')")
    public NoteResponse create(@Valid @RequestBody NoteRequest request, Authentication authentication) {
        return patientNoteService.create(request, authentication.getName());
    }
}
