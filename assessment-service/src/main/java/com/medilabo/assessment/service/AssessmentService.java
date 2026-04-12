package com.medilabo.assessment.service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.medilabo.assessment.config.DownstreamProperties;
import com.medilabo.assessment.dto.AssessmentResponse;
import com.medilabo.assessment.dto.NoteClientResponse;
import com.medilabo.assessment.dto.PatientClientResponse;
import com.medilabo.assessment.dto.RiskLevel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AssessmentService {

    private static final List<String> TRIGGERS = List.of(
            "Hémoglobine A1C",
            "Microalbumine",
            "Taille",
            "Poids",
            "Fumeur",
            "Fumeuse",
            "Anormal",
            "Cholestérol",
            "Vertiges",
            "Rechute",
            "Réaction",
            "Anticorps"
    );

    private final RestClient restClient;
    private final DownstreamProperties downstreamProperties;

    public AssessmentService(RestClient restClient, DownstreamProperties downstreamProperties) {
        this.restClient = restClient;
        this.downstreamProperties = downstreamProperties;
    }

    public AssessmentResponse assess(String patientId, String authorizationHeader) {
        PatientClientResponse patient = restClient.get()
                .uri(downstreamProperties.patientServiceBaseUrl() + "/api/patients/{id}", patientId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .body(PatientClientResponse.class);

        List<NoteClientResponse> notes = restClient.get()
                .uri(downstreamProperties.noteServiceBaseUrl() + "/api/notes/patient/{patientId}", patientId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        int age = calculateAge(patient.birthDate());
        int triggerCount = countTriggers(notes);
        Set<String> matchedTriggers = collectMatchedTriggers(notes);
        RiskLevel riskLevel = computeRisk(patient.gender(), age, triggerCount);

        return new AssessmentResponse(
                patient.id(),
                patient.firstName(),
                patient.lastName(),
                age,
                patient.gender(),
                notes == null ? 0 : notes.size(),
                triggerCount,
                List.copyOf(matchedTriggers),
                riskLevel.label()
        );
    }

    public RiskLevel computeRisk(String gender, int age, int triggerCount) {
        boolean thirtyOrMore = age >= 30;
        boolean male = "M".equalsIgnoreCase(gender);

        if (triggerCount <= 1) {
            return RiskLevel.NONE;
        }

        if (thirtyOrMore) {
            if (triggerCount >= 8) {
                return RiskLevel.EARLY_ONSET;
            }
            if (triggerCount >= 6) {
                return RiskLevel.IN_DANGER;
            }
            if (triggerCount >= 2) {
                return RiskLevel.BORDERLINE;
            }
            return RiskLevel.NONE;
        }

        if (male) {
            if (triggerCount >= 5) {
                return RiskLevel.EARLY_ONSET;
            }
            if (triggerCount >= 3) {
                return RiskLevel.IN_DANGER;
            }
            return RiskLevel.NONE;
        }

        if (triggerCount >= 7) {
            return RiskLevel.EARLY_ONSET;
        }
        if (triggerCount >= 4) {
            return RiskLevel.IN_DANGER;
        }
        return RiskLevel.NONE;
    }

    private int calculateAge(LocalDate birthDate) {
        return birthDate == null ? 0 : Period.between(birthDate, LocalDate.now()).getYears();
    }

    private int countTriggers(List<NoteClientResponse> notes) {
        if (notes == null || notes.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (NoteClientResponse note : notes) {
            String normalizedContent = normalize(note.content());
            for (String trigger : TRIGGERS) {
                if (normalizedContent.contains(normalize(trigger))) {
                    count++;
                }
            }
        }
        return count;
    }

    private Set<String> collectMatchedTriggers(List<NoteClientResponse> notes) {
        Set<String> matched = new LinkedHashSet<>();
        if (notes == null || notes.isEmpty()) {
            return matched;
        }

        for (NoteClientResponse note : notes) {
            String normalizedContent = normalize(note.content());
            for (String trigger : TRIGGERS) {
                if (normalizedContent.contains(normalize(trigger))) {
                    matched.add(trigger);
                }
            }
        }
        return matched;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
