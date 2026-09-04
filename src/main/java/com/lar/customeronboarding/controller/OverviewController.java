package com.lar.customeronboarding.controller;

import com.lar.customeronboarding.dto.response.OverviewResponse;
import com.lar.customeronboarding.service.OverviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Overview", description = "Account overview for the authenticated customer")
public class OverviewController {

    private final OverviewService overviewService;

    @Operation(summary = "Get account overview",
            description = "Returns the authenticated customer's accounts with IBAN, type, balance and currency. "
                    + "Requires the JWT issued at login.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account overview returned"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token")
    })
    @GetMapping("/overview")
    public OverviewResponse overview(@AuthenticationPrincipal Jwt jwt) {
        return overviewService.overviewFor(UUID.fromString(jwt.getSubject()));
    }

}
