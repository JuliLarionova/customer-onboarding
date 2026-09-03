package com.lar.customeronboarding.dto.request;

import com.lar.customeronboarding.dto.AddressDto;
import com.lar.customeronboarding.validation.MinimumAge;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record RegisterCustomerRequest(

        @NotBlank
        @Size(min = 3, max = 50)
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
                message = "username may only contain letters, digits, dot, underscore or hyphen")
        String username,

        @NotBlank
        @Size(max = 100)
        String firstName,

        @NotBlank
        @Size(max = 100)
        String lastName,

        @NotNull
        @Past
        @MinimumAge
        LocalDate dateOfBirth,

        @NotNull
        @Valid
        AddressRequest address
) {}