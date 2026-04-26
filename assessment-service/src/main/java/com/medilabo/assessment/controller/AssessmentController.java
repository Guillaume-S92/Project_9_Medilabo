package com.medilabo.assessment.controller;

import com.medilabo.assessment.dto.AssessmentResponse;
import com.medilabo.assessment.service.AssessmentService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'PRACTITIONER')")
    public AssessmentResponse assess(@PathVariable String patientId,
                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return assessmentService.assess(patientId, authorizationHeader);
    }
}
