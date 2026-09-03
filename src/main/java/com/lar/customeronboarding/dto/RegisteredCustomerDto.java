package com.lar.customeronboarding.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RegisteredCustomerDto(
        UUID id,
        String username,
        String password
) {}