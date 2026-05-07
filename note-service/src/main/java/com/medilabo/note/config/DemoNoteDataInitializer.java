package com.medilabo.note.config;

import com.medilabo.note.document.PatientNoteDocument;
import com.medilabo.note.repository.PatientNoteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.List;

@Configuration
public class DemoNoteDataInitializer {

    @Bean
    ApplicationRunner initializeDemoNotes(
            PatientNoteRepository patientNoteRepository,
            @Value("${app.demo-data.enabled:true}") boolean demoDataEnabled
    ) {
        return args -> {
            if (!demoDataEnabled) {
                return;
            }

            Instant now = Instant.now();

            List<PatientNoteDocument> notes = List.of(
                    note(
                            "1",
                            "Le patient déclare qu'il 'se sent très bien'\nPoids égal ou inférieur au poids recommandé",
                            now
                    ),

                    note(
                            "2",
                            "Le patient déclare qu'il ressent beaucoup de stress au travail\nIl se plaint également que son audition est anormale dernièrement",
                            now
                    ),
                    note(
                            "2",
                            "Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois\nIl remarque également que son audition continue d'être anormale",
                            now
                    ),

                    note(
                            "3",
                            "Le patient déclare qu'il fume depuis peu",
                            now
                    ),
                    note(
                            "3",
                            "Le patient déclare qu'il est fumeur et qu'il a cessé de fumer l'année dernière\nIl se plaint également de crises d’apnée respiratoire anormales\nTests de laboratoire indiquant un taux de cholestérol LDL élevé",
                            now
                    ),

                    note(
                            "4",
                            "Le patient déclare qu'il lui est devenu difficile de monter les escaliers\nIl se plaint également d’être essoufflé\nTests de laboratoire indiquant que les anticorps sont élevés\nRéaction aux médicaments",
                            now
                    ),
                    note(
                            "4",
                            "Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps",
                            now
                    ),
                    note(
                            "4",
                            "Le patient déclare avoir commencé à fumer depuis peu\nHémoglobine A1C supérieure au niveau recommandé",
                            now
                    ),
                    note(
                            "4",
                            "Taille, Poids, Cholestérol, Vertige et Réaction",
                            now
                    )
            );

            for (PatientNoteDocument note : notes) {
                if (!noteAlreadyExists(patientNoteRepository, note)) {
                    patientNoteRepository.save(note);
                }
            }
        };
    }

    private PatientNoteDocument note(String patientId, String content, Instant now) {
        return new PatientNoteDocument()
                .setPatientId(patientId)
                .setContent(content)
                .setPractitionerUsername("practitioner")
                .setCreatedAt(now)
                .setUpdatedAt(now);
    }

    private boolean noteAlreadyExists(PatientNoteRepository repository, PatientNoteDocument note) {
        return repository.findByPatientIdOrderByCreatedAtDesc(note.getPatientId())
                .stream()
                .anyMatch(existingNote -> note.getContent().equals(existingNote.getContent()));
    }
}