package com.lar.customeronboarding.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(
    String username,
    String status, 
	String token
) {}