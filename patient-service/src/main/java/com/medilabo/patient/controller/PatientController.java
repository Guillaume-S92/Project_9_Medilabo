package com.medilabo.patient.controller;

import java.util.List;

import com.medilabo.patient.dto.PatientRequest;
import com.medilabo.patient.dto.PatientResponse;
import com.medilabo.patient.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ORGANIZER', 'PRACTITIONER')")
    public List<PatientResponse> findAll() {
        return patientService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'PRACTITIONER')")
    public PatientResponse findById(@PathVariable String id) {
        return patientService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ORGANIZER')")
    public PatientResponse create(@Valid @RequestBody PatientRequest request) {
        return patientService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public PatientResponse update(@PathVariable String id, @Valid @RequestBody PatientRequest request) {
        return patientService.update(id, request);
    }
}
