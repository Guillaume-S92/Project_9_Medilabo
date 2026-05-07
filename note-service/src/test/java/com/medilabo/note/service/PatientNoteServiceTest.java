package com.medilabo.note.service;

import com.medilabo.note.document.PatientNoteDocument;
import com.medilabo.note.dto.NoteRequest;
import com.medilabo.note.dto.NoteResponse;
import com.medilabo.note.repository.PatientNoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientNoteServiceTest {

    @Mock
    private PatientNoteRepository patientNoteRepository;

    @InjectMocks
    private PatientNoteService patientNoteService;

    private PatientNoteDocument noteDocument;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();
        noteDocument = new PatientNoteDocument()
                .setId("note-1")
                .setPatientId("uuid-1")
                .setContent("Le patient présente un taux de Cholestérol élevé.")
                .setPractitionerUsername("practitioner")
                .setCreatedAt(now)
                .setUpdatedAt(now);
    }

    // Vérifie que findAll retourne toutes les notes triées par date décroissante
    @Test
    void shouldReturnAllNotesSortedByDateDesc() {
        Instant earlier = Instant.now().minusSeconds(3600);
        PatientNoteDocument older = new PatientNoteDocument()
                .setId("note-2")
                .setPatientId("uuid-2")
                .setContent("Ancienne note")
                .setPractitionerUsername("practitioner")
                .setCreatedAt(earlier)
                .setUpdatedAt(earlier);

        when(patientNoteRepository.findAll()).thenReturn(List.of(older, noteDocument));

        List<NoteResponse> result = patientNoteService.findAll();

        assertThat(result).hasSize(2);
        // La note la plus récente doit être en premier
        assertThat(result.getFirst().id()).isEqualTo("note-1");
    }

    // Vérifie que findByPatientId retourne uniquement les notes du patient donné
    @Test
    void shouldReturnNotesByPatientId() {
        when(patientNoteRepository.findByPatientIdOrderByCreatedAtDesc("uuid-1"))
                .thenReturn(List.of(noteDocument));

        List<NoteResponse> result = patientNoteService.findByPatientId("uuid-1");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().patientId()).isEqualTo("uuid-1");
        assertThat(result.getFirst().content()).contains("Cholestérol");
    }

    // Vérifie que findByPatientId retourne une liste vide si aucune note n'existe pour ce patient
    @Test
    void shouldReturnEmptyListWhenNoNotesForPatient() {
        when(patientNoteRepository.findByPatientIdOrderByCreatedAtDesc("unknown"))
                .thenReturn(List.of());

        List<NoteResponse> result = patientNoteService.findByPatientId("unknown");

        assertThat(result).isEmpty();
    }

    // Vérifie que create sauvegarde la note avec le bon username du praticien
    @Test
    void shouldCreateNoteWithPractitionerUsername() {
        when(patientNoteRepository.save(any(PatientNoteDocument.class))).thenReturn(noteDocument);

        NoteRequest request = new NoteRequest("uuid-1", "Le patient présente un taux de Cholestérol élevé.");
        NoteResponse result = patientNoteService.create(request, "practitioner");

        assertThat(result.practitionerUsername()).isEqualTo("practitioner");
        assertThat(result.patientId()).isEqualTo("uuid-1");
        verify(patientNoteRepository, times(1)).save(any(PatientNoteDocument.class));
    }

    // Vérifie que delete supprime la note quand elle existe
    @Test
    void shouldDeleteExistingNote() {
        when(patientNoteRepository.existsById("note-1")).thenReturn(true);

        patientNoteService.delete("note-1");

        verify(patientNoteRepository).deleteById("note-1");
    }

    // Vérifie qu'une exception 404 est levée si on tente de supprimer une note inexistante
    @Test
    void shouldThrow404WhenDeletingNonExistentNote() {
        when(patientNoteRepository.existsById("unknown")).thenReturn(false);

        assertThatThrownBy(() -> patientNoteService.delete("unknown"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Note introuvable");

        verify(patientNoteRepository, never()).deleteById(any());
    }
}
