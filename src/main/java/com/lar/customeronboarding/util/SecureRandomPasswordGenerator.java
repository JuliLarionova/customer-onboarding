package com.lar.customeronboarding.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class SecureRandomPasswordGenerator implements PasswordGenerator {

    private static final String ALPHABET =
            "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LENGTH = 12;

    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate() {
        var sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

}
