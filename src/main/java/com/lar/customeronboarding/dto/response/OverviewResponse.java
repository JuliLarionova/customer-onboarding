package com.lar.customeronboarding.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "Account overview for the authenticated customer")
public record OverviewResponse(

        List<AccountSummary> accounts
) {}
