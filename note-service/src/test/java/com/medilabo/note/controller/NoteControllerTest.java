package com.medilabo.note.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.note.dto.NoteRequest;
import com.medilabo.note.dto.NoteResponse;
import com.medilabo.note.service.PatientNoteService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
@Import(NoteControllerTest.MethodSecurityConfig.class)
class NoteControllerTest {

    // Configuration minimale pour activer @PreAuthorize dans le contexte de test WebMvcTest
    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityConfig {}

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PatientNoteService patientNoteService;

    // Mock nécessaire car la config Spring Security attend un JwtDecoder
    @MockitoBean
    private JwtDecoder jwtDecoder;

    private NoteResponse noteResponse;
    private NoteRequest noteRequest;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();
        noteResponse = new NoteResponse(
                "note-1", "uuid-1",
                "Le patient présente un taux de Cholestérol élevé.",
                "practitioner", now, now
        );
        noteRequest = new NoteRequest("uuid-1", "Le patient présente un taux de Cholestérol élevé.");
    }

    // Vérifie que GET /api/notes retourne 200 et la liste des notes pour un PRACTITIONER
    @Test
    @WithMockUser(roles = "PRACTITIONER")
    void shouldReturn200WithAllNotesForPractitioner() throws Exception {
        when(patientNoteService.findAll()).thenReturn(List.of(noteResponse));

        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("note-1"))
                .andExpect(jsonPath("$[0].patientId").value("uuid-1"));
    }

    // Vérifie que GET /api/notes retourne 403 si l'utilisateur n'est pas PRACTITIONER
    @Test
    @WithMockUser(roles = "ORGANIZER")
    void shouldReturn403WhenOrganizerAccessesNotes() throws Exception {
        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isForbidden());
    }

    // Vérifie que GET /api/notes/patient/:id retourne les notes du patient
    @Test
    @WithMockUser(roles = "PRACTITIONER")
    void shouldReturn200WithNotesByPatientId() throws Exception {
        when(patientNoteService.findByPatientId("uuid-1")).thenReturn(List.of(noteResponse));

        mockMvc.perform(get("/api/notes/patient/uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value("uuid-1"));
    }

    // Vérifie que POST /api/notes crée une note et retourne 201 pour un PRACTITIONER
    @Test
    @WithMockUser(username = "practitioner", roles = "PRACTITIONER")
    void shouldReturn201WhenPractitionerCreatesNote() throws Exception {
        when(patientNoteService.create(any(NoteRequest.class), eq("practitioner")))
                .thenReturn(noteResponse);

        mockMvc.perform(post("/api/notes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.practitionerUsername").value("practitioner"));
    }

    // Vérifie que DELETE /api/notes/:id retourne 204 pour un PRACTITIONER
    @Test
    @WithMockUser(roles = "PRACTITIONER")
    void shouldReturn204WhenPractitionerDeletesNote() throws Exception {
        doNothing().when(patientNoteService).delete("note-1");

        mockMvc.perform(delete("/api/notes/note-1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    // Vérifie que tout accès sans authentification retourne 401
    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isUnauthorized());
    }
}
