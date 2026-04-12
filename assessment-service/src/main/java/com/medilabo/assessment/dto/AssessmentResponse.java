package com.medilabo.assessment.dto;

import java.util.List;

public record AssessmentResponse(
        String patientId,
        String patientFirstName,
        String patientLastName,
        int age,
        String gender,
        int noteCount,
        int triggerCount,
        List<String> matchedTriggers,
        String riskLevel
) {
}
