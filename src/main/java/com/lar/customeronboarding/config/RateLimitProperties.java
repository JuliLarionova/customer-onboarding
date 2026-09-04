package com.lar.customeronboarding.config;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.rate-limit")
@Validated
public record RateLimitProperties(

        boolean enabled,

        @Positive
        int capacity,

        @Positive
        int refillTokens,

        @NotNull
        Duration refillPeriod
) {

        @AssertTrue(message = "Refill tokens must not exceed capacity")
        boolean isRefillWithinCapacity() {
                return refillTokens <= capacity;
        }

}
