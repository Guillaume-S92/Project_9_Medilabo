package com.medilabo.patient.service;

import com.medilabo.patient.dto.PatientRequest;
import com.medilabo.patient.dto.PatientResponse;
import com.medilabo.patient.entity.Gender;
import com.medilabo.patient.entity.PatientEntity;
import com.medilabo.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private PatientEntity patientEntity;
    private PatientRequest patientRequest;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();
        patientEntity = new PatientEntity()
                .setId("uuid-1")
                .setFirstName("Jean")
                .setLastName("Dupont")
                .setBirthDate(LocalDate.of(1980, 1, 15))
                .setGender(Gender.M)
                .setAddress("10 rue de la Paix")
                .setPhone("0600000000")
                .setCreatedAt(now)
                .setUpdatedAt(now);

        patientRequest = new PatientRequest(
                "Jean", "Dupont",
                LocalDate.of(1980, 1, 15),
                Gender.M,
                "10 rue de la Paix",
                "0600000000"
        );
    }

    // Vérifie que findAll retourne bien la liste complète des patients
    @Test
    void shouldReturnAllPatients() {
        when(patientRepository.findAll()).thenReturn(List.of(patientEntity));

        List<PatientResponse> result = patientService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("Jean");
        assertThat(result.get(0).lastName()).isEqualTo("Dupont");
    }

    // Vérifie que findAll retourne une liste vide si aucun patient en base
    @Test
    void shouldReturnEmptyListWhenNoPatientsExist() {
        when(patientRepository.findAll()).thenReturn(List.of());

        List<PatientResponse> result = patientService.findAll();

        assertThat(result).isEmpty();
    }

    // Vérifie que findById retourne le patient correspondant à l'id donné
    @Test
    void shouldReturnPatientById() {
        when(patientRepository.findById("uuid-1")).thenReturn(Optional.of(patientEntity));

        PatientResponse result = patientService.findById("uuid-1");

        assertThat(result.id()).isEqualTo("uuid-1");
        assertThat(result.gender()).isEqualTo(Gender.M);
    }

    // Vérifie qu'une exception 404 est levée si le patient n'existe pas
    @Test
    void shouldThrow404WhenPatientNotFound() {
        when(patientRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.findById("unknown"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Patient introuvable");
    }

    // Vérifie que create sauvegarde et retourne le nouveau patient
    @Test
    void shouldCreatePatientAndReturnResponse() {
        when(patientRepository.save(any(PatientEntity.class))).thenReturn(patientEntity);

        PatientResponse result = patientService.create(patientRequest);

        assertThat(result.firstName()).isEqualTo("Jean");
        verify(patientRepository, times(1)).save(any(PatientEntity.class));
    }

    // Vérifie que update modifie le patient existant et le sauvegarde
    @Test
    void shouldUpdateExistingPatient() {
        when(patientRepository.findById("uuid-1")).thenReturn(Optional.of(patientEntity));
        when(patientRepository.save(any(PatientEntity.class))).thenReturn(patientEntity);

        PatientRequest updateRequest = new PatientRequest(
                "Jean", "Dupont",
                LocalDate.of(1980, 1, 15),
                Gender.M,
                "Nouvelle adresse",
                "0700000000"
        );

        PatientResponse result = patientService.update("uuid-1", updateRequest);

        assertThat(result.id()).isEqualTo("uuid-1");
        verify(patientRepository).save(any(PatientEntity.class));
    }

    // Vérifie qu'une exception 404 est levée si on tente de mettre à jour un patient inexistant
    @Test
    void shouldThrow404WhenUpdatingNonExistentPatient() {
        when(patientRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.update("unknown", patientRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Patient introuvable");
    }

    // Vérifie que delete supprime bien le patient quand il existe
    @Test
    void shouldDeleteExistingPatient() {
        when(patientRepository.existsById("uuid-1")).thenReturn(true);

        patientService.delete("uuid-1");

        verify(patientRepository).deleteById("uuid-1");
    }

    // Vérifie qu'une exception 404 est levée si on tente de supprimer un patient inexistant
    @Test
    void shouldThrow404WhenDeletingNonExistentPatient() {
        when(patientRepository.existsById("unknown")).thenReturn(false);

        assertThatThrownBy(() -> patientService.delete("unknown"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Patient introuvable");

        verify(patientRepository, never()).deleteById(any());
    }
}
