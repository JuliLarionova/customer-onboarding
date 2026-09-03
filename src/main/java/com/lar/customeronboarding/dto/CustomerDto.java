package com.lar.customeronboarding.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CustomerDto(
        String username,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        AddressDto address
) {}
