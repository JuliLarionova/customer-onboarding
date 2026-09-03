package com.lar.customeronboarding.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Schema(description = "Credentials returned once after successful registration")
@Builder
public record RegisterCustomerResponse(

        @Schema(description = "Identifier of the newly registered customer",
                example = "6f1c2a4e-9b3d-4c5a-8e7f-1a2b3c4d5e6f")
        UUID customerId,

        @Schema(description = "The registered username", example = "JSmith")
        String username,

        @Schema(description = "Generated plaintext password; returned only in this response "
                + "and never retrievable again")
        String password

) {}