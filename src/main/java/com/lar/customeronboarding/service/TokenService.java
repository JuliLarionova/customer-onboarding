package com.lar.customeronboarding.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

//@Service
public class TokenService {

    private static final int TOKEN_BYTE_LENGTH = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final Cache<String, String> tokenStore = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .maximumSize(100_000)
        .build();

    public String issueToken(String username) {
        byte[] randomBytes = new byte[TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        tokenStore.put(token, username);
        return token;
    }

    public Optional<String> resolveUsername(String token) {
        return Optional.ofNullable(tokenStore.getIfPresent(token));
    }

    public void invalidate(String token) {
        tokenStore.invalidate(token);
    }
}
