package com.lar.customeronboarding.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AddressRequest(

        @NotBlank
        @Size(max = 150)
        String street,

        @NotBlank
        @Size(max = 20)
        String houseNumber,

        @NotBlank
        @Size(max = 20)
        String postalCode,

        @NotBlank
        @Size(max = 100)
        String city,

        @NotBlank
        @Pattern(regexp = "^[A-Z]{2}$")
        String countryCode
) {}
