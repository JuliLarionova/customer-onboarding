package com.lar.customeronboarding.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RegisterCustomerResponse(
        UUID customerId,
        String username,
        String password
) {}