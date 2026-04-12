package com.medilabo.assessment.service;

import com.medilabo.assessment.config.DownstreamProperties;
import com.medilabo.assessment.dto.RiskLevel;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentServiceTest {

    private final AssessmentService assessmentService =
            new AssessmentService(RestClient.builder().build(), new DownstreamProperties("http://patient", "http://note"));

    @Test
    void shouldReturnNoneWhenZeroOrOneTrigger() {
        assertThat(assessmentService.computeRisk("M", 42, 0)).isEqualTo(RiskLevel.NONE);
        assertThat(assessmentService.computeRisk("F", 42, 1)).isEqualTo(RiskLevel.NONE);
    }

    @Test
    void shouldReturnBorderlineForAdultBetweenTwoAndFiveTriggers() {
        assertThat(assessmentService.computeRisk("F", 45, 2)).isEqualTo(RiskLevel.BORDERLINE);
        assertThat(assessmentService.computeRisk("M", 45, 5)).isEqualTo(RiskLevel.BORDERLINE);
    }

    @Test
    void shouldReturnInDangerForYoungMaleFromThreeTriggers() {
        assertThat(assessmentService.computeRisk("M", 25, 3)).isEqualTo(RiskLevel.IN_DANGER);
        assertThat(assessmentService.computeRisk("M", 25, 4)).isEqualTo(RiskLevel.IN_DANGER);
    }

    @Test
    void shouldReturnEarlyOnsetForYoungFemaleFromSevenTriggers() {
        assertThat(assessmentService.computeRisk("F", 25, 7)).isEqualTo(RiskLevel.EARLY_ONSET);
    }

    @Test
    void shouldReturnEarlyOnsetForAdultsFromEightTriggers() {
        assertThat(assessmentService.computeRisk("M", 60, 8)).isEqualTo(RiskLevel.EARLY_ONSET);
    }
}
