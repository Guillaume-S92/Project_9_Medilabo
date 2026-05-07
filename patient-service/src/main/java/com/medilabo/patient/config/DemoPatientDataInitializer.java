package com.medilabo.patient.config;

import com.medilabo.patient.entity.Gender;
import com.medilabo.patient.entity.PatientEntity;
import com.medilabo.patient.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DemoPatientDataInitializer {

    @Bean
    ApplicationRunner initializeDemoPatients(
            PatientRepository patientRepository,
            @Value("${app.demo-data.enabled:true}") boolean demoDataEnabled
    ) {
        return args -> {
            if (!demoDataEnabled) {
                return;
            }

            Instant now = Instant.now();

            List<PatientEntity> patients = List.of(
                    new PatientEntity()
                            .setId("1")
                            .setFirstName("Test")
                            .setLastName("TestNone")
                            .setBirthDate(LocalDate.of(1966, 12, 31))
                            .setGender(Gender.F)
                            .setAddress("1 Brookside St")
                            .setPhone("100-222-3333")
                            .setCreatedAt(now)
                            .setUpdatedAt(now),

                    new PatientEntity()
                            .setId("2")
                            .setFirstName("Test")
                            .setLastName("TestBorderline")
                            .setBirthDate(LocalDate.of(1945, 6, 24))
                            .setGender(Gender.M)
                            .setAddress("2 High St")
                            .setPhone("200-333-4444")
                            .setCreatedAt(now)
                            .setUpdatedAt(now),

                    new PatientEntity()
                            .setId("3")
                            .setFirstName("Test")
                            .setLastName("TestInDanger")
                            .setBirthDate(LocalDate.of(2004, 6, 18))
                            .setGender(Gender.M)
                            .setAddress("3 Club Road")
                            .setPhone("300-444-5555")
                            .setCreatedAt(now)
                            .setUpdatedAt(now),

                    new PatientEntity()
                            .setId("4")
                            .setFirstName("Test")
                            .setLastName("TestEarlyOnset")
                            .setBirthDate(LocalDate.of(2002, 6, 28))
                            .setGender(Gender.F)
                            .setAddress("4 Valley Dr")
                            .setPhone("400-555-6666")
                            .setCreatedAt(now)
                            .setUpdatedAt(now)
            );

            for (PatientEntity patient : patients) {
                if (!patientRepository.existsById(patient.getId())) {
                    patientRepository.save(patient);
                }
            }
        };
    }
}