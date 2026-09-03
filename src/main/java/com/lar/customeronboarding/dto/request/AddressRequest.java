package com.lar.customeronboarding.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Residential address of the customer")
public record AddressRequest(

        @Schema(description = "Street name", example = "Appelstraat")
        @NotBlank
        @Size(max = 150)
        String street,

        @Schema(description = "House number, including suffixes", example = "159A")
        @NotBlank
        @Size(max = 20)
        String houseNumber,

        @Schema(description = "Postal code", example = "1131AB")
        @NotBlank
        @Size(max = 20)
        String postalCode,

        @Schema(description = "City", example = "Amsterdam")
        @NotBlank
        @Size(max = 100)
        String city,

        @Schema(description = "Two-letter uppercase ISO 3166-1 country code",
                example = "NL", pattern = "^[A-Z]{2}$")
        @NotBlank
        @Pattern(regexp = "^[A-Z]{2}$")
        String countryCode
) {}
