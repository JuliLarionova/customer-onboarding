package com.lar.customeronboarding.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Issued bearer token")
public record LoginResponse(

		@Schema(description = "JWT to send as 'Authorization: Bearer <token>'")
		String token

) {}