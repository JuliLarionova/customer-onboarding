package com.lar.customeronboarding.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static com.lar.customeronboarding.support.testdata.CustomerTestDataProvider.*;
import static org.junit.jupiter.api.Assertions.*;

public class TokenServiceTest {

    private static final String JWT_TEST_SECRET = "0123456789abcdef0123456789abcdef";
    private TokenService tokenService;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        var key = new SecretKeySpec(JWT_TEST_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        var properties = new JwtProperties(JWT_TEST_SECRET, Duration.ofMinutes(15));
        tokenService = new TokenService(new NimbusJwtEncoder(new ImmutableSecret<>(key)), properties);
        jwtDecoder = NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Test
    void issuesDecodableTokenWithCustomerClaims() {
        var customer = customerBuilder().build();
        ReflectionTestUtils.setField(customer, "id", CUSTOMER_ID);

        var token = tokenService.issueFor(customer);

        var jwt = jwtDecoder.decode(token);
        assertAll(
                () -> assertEquals(CUSTOMER_ID.toString(), jwt.getSubject()),
                () -> assertEquals(USERNAME, jwt.getClaim("username")),
                () -> assertEquals("customer-onboarding", jwt.getClaimAsString("iss"))
        );
    }

    @Test
    void setsExpiryFromConfiguredTimeToLive() {
        var customer = customerBuilder().build();
        ReflectionTestUtils.setField(customer, "id", CUSTOMER_ID);

        var jwt = jwtDecoder.decode(tokenService.issueFor(customer));

        var lifetime = Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt());
        assertEquals(Duration.ofMinutes(15), lifetime);
    }

    @Test
    void tokenSignedWithDifferentSecretIsRejected() {
        var customer = customerBuilder().build();
        ReflectionTestUtils.setField(customer, "id", CUSTOMER_ID);
        var token = tokenService.issueFor(customer);

        var otherKey = new SecretKeySpec(
                "a-completely-different-32-byte-secret!!!".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        var strangerDecoder = NimbusJwtDecoder.withSecretKey(otherKey).build();

        assertThrows(JwtException.class, () -> strangerDecoder.decode(token));
    }

}
