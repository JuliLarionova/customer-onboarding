package com.lar.customeronboarding.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@ConfigurationProperties(prefix = "app.registration")
@Validated
public record RegistrationProperties(
        @NotEmpty Set<@Pattern(regexp = "^[A-Z]{2}$") String> allowedCountries
) {}