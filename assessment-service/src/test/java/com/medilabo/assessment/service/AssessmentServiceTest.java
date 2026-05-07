package com.medilabo.assessment.service;

import com.medilabo.assessment.config.DownstreamProperties;
import com.medilabo.assessment.dto.RiskLevel;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentServiceTest {

    private final AssessmentService assessmentService =
            new AssessmentService(RestClient.builder().build(), new DownstreamProperties("http://patient", "http://note"));

    // Vérifie que 0 ou 1 déclencheur donne un risque NONE (aucun risque)
    @Test
    void shouldReturnNoneWhenZeroOrOneTrigger() {
        assertThat(assessmentService.computeRisk("M", 42, 0)).isEqualTo(RiskLevel.NONE);
        assertThat(assessmentService.computeRisk("F", 42, 1)).isEqualTo(RiskLevel.NONE);
    }

    // Vérifie qu'un adulte (>= 30 ans) avec 2 à 5 déclencheurs est BORDERLINE
    @Test
    void shouldReturnBorderlineForAdultBetweenTwoAndFiveTriggers() {
        assertThat(assessmentService.computeRisk("F", 45, 2)).isEqualTo(RiskLevel.BORDERLINE);
        assertThat(assessmentService.computeRisk("M", 45, 5)).isEqualTo(RiskLevel.BORDERLINE);
    }

    // Vérifie qu'un adulte avec 6 ou 7 déclencheurs est IN_DANGER
    @Test
    void shouldReturnInDangerForAdultWithSixOrSevenTriggers() {
        assertThat(assessmentService.computeRisk("M", 45, 6)).isEqualTo(RiskLevel.IN_DANGER);
        assertThat(assessmentService.computeRisk("F", 50, 7)).isEqualTo(RiskLevel.IN_DANGER);
    }

    // Vérifie qu'un homme jeune (< 30 ans) avec 3 ou 4 déclencheurs est IN_DANGER
    @Test
    void shouldReturnInDangerForYoungMaleFromThreeTriggers() {
        assertThat(assessmentService.computeRisk("M", 25, 3)).isEqualTo(RiskLevel.IN_DANGER);
        assertThat(assessmentService.computeRisk("M", 25, 4)).isEqualTo(RiskLevel.IN_DANGER);
    }

    // Vérifie qu'un homme jeune avec 5 déclencheurs ou plus est EARLY_ONSET
    @Test
    void shouldReturnEarlyOnsetForYoungMaleFromFiveTriggers() {
        assertThat(assessmentService.computeRisk("M", 25, 5)).isEqualTo(RiskLevel.EARLY_ONSET);
    }

    // Vérifie qu'une femme jeune (< 30 ans) avec 4 à 6 déclencheurs est IN_DANGER
    @Test
    void shouldReturnInDangerForYoungFemaleFromFourTriggers() {
        assertThat(assessmentService.computeRisk("F", 25, 4)).isEqualTo(RiskLevel.IN_DANGER);
        assertThat(assessmentService.computeRisk("F", 25, 6)).isEqualTo(RiskLevel.IN_DANGER);
    }

    // Vérifie qu'une femme jeune avec 7 déclencheurs ou plus est EARLY_ONSET
    @Test
    void shouldReturnEarlyOnsetForYoungFemaleFromSevenTriggers() {
        assertThat(assessmentService.computeRisk("F", 25, 7)).isEqualTo(RiskLevel.EARLY_ONSET);
    }

    // Vérifie qu'un adulte avec 8 déclencheurs ou plus est EARLY_ONSET (cas critique)
    @Test
    void shouldReturnEarlyOnsetForAdultsFromEightTriggers() {
        assertThat(assessmentService.computeRisk("M", 60, 8)).isEqualTo(RiskLevel.EARLY_ONSET);
        assertThat(assessmentService.computeRisk("F", 35, 8)).isEqualTo(RiskLevel.EARLY_ONSET);
    }

    // Vérifie la logique de la frontière d'âge : 29 ans = jeune, 30 ans = adulte
    @Test
    void shouldApplyCorrectAgeThresholdAtThirty() {
        // À 29 ans un homme avec 3 déclencheurs = IN_DANGER (règle jeune)
        assertThat(assessmentService.computeRisk("M", 29, 3)).isEqualTo(RiskLevel.IN_DANGER);
        // À 30 ans un homme avec 3 déclencheurs = BORDERLINE (règle adulte)
        assertThat(assessmentService.computeRisk("M", 30, 3)).isEqualTo(RiskLevel.BORDERLINE);
    }
}
