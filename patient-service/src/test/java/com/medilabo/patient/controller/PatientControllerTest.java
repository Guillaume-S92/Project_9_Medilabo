package com.medilabo.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.patient.dto.PatientRequest;
import com.medilabo.patient.dto.PatientResponse;
import com.medilabo.patient.entity.Gender;
import com.medilabo.patient.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@Import(PatientControllerTest.MethodSecurityConfig.class)
class PatientControllerTest {

    // Configuration minimale pour activer @PreAuthorize dans le contexte de test WebMvcTest
    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityConfig {}

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PatientService patientService;

    // Mock nécessaire car la config Spring Security attend un JwtDecoder
    @MockitoBean
    private JwtDecoder jwtDecoder;

    private PatientResponse patientResponse;
    private PatientRequest patientRequest;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();
        patientResponse = new PatientResponse(
                "uuid-1", "Jean", "Dupont",
                LocalDate.of(1980, 1, 15),
                Gender.M, "10 rue de la Paix", "0600000000",
                now, now
        );
        patientRequest = new PatientRequest(
                "Jean", "Dupont",
                LocalDate.of(1980, 1, 15),
                Gender.M, "10 rue de la Paix", "0600000000"
        );
    }

    // Vérifie que GET /api/patients retourne 200 et la liste pour un ORGANIZER
    @Test
    @WithMockUser(roles = "ORGANIZER")
    void shouldReturn200WithPatientListForOrganizer() throws Exception {
        when(patientService.findAll()).thenReturn(List.of(patientResponse));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Jean"))
                .andExpect(jsonPath("$[0].lastName").value("Dupont"));
    }

    // Vérifie que GET /api/patients/:id retourne 200 et le patient pour un PRACTITIONER
    @Test
    @WithMockUser(roles = "PRACTITIONER")
    void shouldReturn200WithPatientByIdForPractitioner() throws Exception {
        when(patientService.findById("uuid-1")).thenReturn(patientResponse);

        mockMvc.perform(get("/api/patients/uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("uuid-1"));
    }

    // Vérifie que POST /api/patients retourne 201 uniquement pour un ORGANIZER
    @Test
    @WithMockUser(roles = "ORGANIZER")
    void shouldReturn201WhenOrganizerCreatesPatient() throws Exception {
        when(patientService.create(any(PatientRequest.class))).thenReturn(patientResponse);

        mockMvc.perform(post("/api/patients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jean"));
    }

    // Vérifie que POST /api/patients retourne 403 pour un PRACTITIONER (pas ORGANIZER)
    @Test
    @WithMockUser(roles = "PRACTITIONER")
    void shouldReturn403WhenPractitionerTriesToCreatePatient() throws Exception {
        mockMvc.perform(post("/api/patients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequest)))
                .andExpect(status().isForbidden());
    }

    // Vérifie que PUT /api/patients/:id retourne 200 et le patient mis à jour
    @Test
    @WithMockUser(roles = "ORGANIZER")
    void shouldReturn200WhenOrganizerUpdatesPatient() throws Exception {
        when(patientService.update(eq("uuid-1"), any(PatientRequest.class))).thenReturn(patientResponse);

        mockMvc.perform(put("/api/patients/uuid-1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("uuid-1"));
    }

    // Vérifie que DELETE /api/patients/:id retourne 204 pour un ORGANIZER
    @Test
    @WithMockUser(roles = "ORGANIZER")
    void shouldReturn204WhenOrganizerDeletesPatient() throws Exception {
        doNothing().when(patientService).delete("uuid-1");

        mockMvc.perform(delete("/api/patients/uuid-1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    // Vérifie que tout accès sans authentification retourne 401
    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isUnauthorized());
    }
}
