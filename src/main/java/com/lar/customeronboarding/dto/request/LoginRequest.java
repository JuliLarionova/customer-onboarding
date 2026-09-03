package com.lar.customeronboarding.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
@Schema(description = "Credentials for logging in")
public record LoginRequest(

		@Schema(example = "JSmith")
		@NotBlank
		String username,

		@Schema(description = "Password issued at registration")
		@NotBlank
		String password
) {}