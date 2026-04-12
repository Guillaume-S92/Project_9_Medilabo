package com.medilabo.assessment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clients")
public record DownstreamProperties(
        String patientServiceBaseUrl,
        String noteServiceBaseUrl
) {
}
