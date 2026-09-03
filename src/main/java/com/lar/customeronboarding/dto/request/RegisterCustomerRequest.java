package com.lar.customeronboarding.dto.request;

import com.lar.customeronboarding.validation.MinimumAge;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(description = "Fields required to register a customer and open an account")
public record RegisterCustomerRequest(

        @Schema(description = "Unique username chosen by the customer",
                example = "JSmith", minLength = 3, maxLength = 50)
        @NotBlank
        @Size(min = 3, max = 50)
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
                message = "username may only contain letters, digits, dot, underscore or hyphen")
        String username,

        @Schema(description = "Customer's first name", example = "Jane")
        @NotBlank
        @Size(max = 100)
        String firstName,

        @Schema(description = "Customer's last name", example = "Smith")
        @NotBlank
        @Size(max = 100)
        String lastName,

        @Schema(description = "Date of birth; customer must be at least 18",
                example = "1990-01-01")
        @NotNull
        @Past
        @MinimumAge
        LocalDate dateOfBirth,

        @Schema(description = "Residential address; registration is limited to allowed countries")
        @NotNull
        @Valid
        AddressRequest address
) {}