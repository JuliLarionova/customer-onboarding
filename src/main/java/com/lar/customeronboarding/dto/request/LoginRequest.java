package com.lar.customeronboarding.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record LoginRequest(
    @NotBlank 
	String username,
	
    @NotBlank 
	String password
) {}