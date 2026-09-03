package com.lar.customeronboarding.security;

import com.lar.customeronboarding.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public String issueFor(Customer customer) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer("customer-onboarding")
                .subject(customer.getId().toString())
                .claim("username", customer.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.timeToLive()))
                .build();

        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

}
