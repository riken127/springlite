package com.example.service.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Stable, typed application identity used in logs and metrics. */
@Validated
@ConfigurationProperties("app")
public record AppProperties(@NotBlank String serviceName, @NotBlank String environment) {}
