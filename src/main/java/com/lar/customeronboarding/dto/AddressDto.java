package com.lar.customeronboarding.dto;

import lombok.Builder;

@Builder
public record AddressDto(
        String street,
        String houseNumber,
        String postalCode,
        String city,
        String countryCode
) {}