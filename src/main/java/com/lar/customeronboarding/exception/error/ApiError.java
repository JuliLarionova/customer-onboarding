package com.lar.customeronboarding.exception.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response returned to the client")
public record ApiError(

        @Schema(description = "When the error occurred", example = "2026-09-03T10:15:30Z")
        Instant timestamp,

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "Generic, client-safe message", example = "Invalid request")
        String error,

        @Schema(description = "Correlation id that matches the server log",
                example = "1c3d8a2e-9b0f-4f7e-8d2a-2b1c9e7f4a55")
        String traceId,

        @Schema(description = "Field-level validation errors; present only for 400 validation failures",
                example = "{\"dateOfBirth\": [\"customer must be at least 18 years old\"]}")
        Map<String, List<String>> errors
) {
}
