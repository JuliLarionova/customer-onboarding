package com.lar.customeronboarding.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountOverviewResponse(
    String accountNumber,
    String accountType,
    BigDecimal balance,
    String currency
) {}