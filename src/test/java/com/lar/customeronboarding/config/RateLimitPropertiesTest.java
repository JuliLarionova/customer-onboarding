package com.lar.customeronboarding.config;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimitPropertiesTest {

    @Test
    void rejectsRefillExceedingCapacity() {
        var properties = new RateLimitProperties(true, 2, 5, Duration.ofSeconds(1));

        assertFalse(properties.isRefillWithinCapacity());
    }

    @Test
    void acceptsRefillWithinCapacity() {
        var properties = new RateLimitProperties(true, 10, 2, Duration.ofSeconds(1));

        assertTrue(properties.isRefillWithinCapacity());
    }

}
