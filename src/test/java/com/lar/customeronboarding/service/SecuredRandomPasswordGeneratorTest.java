package com.lar.customeronboarding.service;

import com.lar.customeronboarding.util.SecureRandomPasswordGenerator;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SecuredRandomPasswordGeneratorTest {

    //TODO: fix magic numbers and strings in tests

    private final SecureRandomPasswordGenerator generator = new SecureRandomPasswordGenerator();

    @Test
    void generatesPasswordOfExpectedLength() {
        assertEquals(12, generator.generate().length());
    }

    @RepeatedTest(20)
    void usesOnlyAllowedCharacters() {
        var password = generator.generate();

        assertTrue(password.matches("^[a-km-zA-HJ-NP-Z2-9]+$"),
                () -> "Unexpected character in: " + password);
    }

    @Test
    void neverContainsAmbiguousCharacters() {
        for (int i = 0; i < 100; i++) {
            var password = generator.generate();
            assertFalse(password.contains("l") || password.contains("I")
                            || password.contains("O") || password.contains("0")
                            || password.contains("1"),
                    () -> "Ambiguous character in: " + password);
        }
    }

    @Test
    void generatesDifferentPasswordsOnConsecutiveCalls() {
        assertNotEquals(generator.generate(), generator.generate());
    }

}
