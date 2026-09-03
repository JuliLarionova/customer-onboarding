package com.lar.customeronboarding.controller;

import com.lar.customeronboarding.dto.request.LoginRequest;
import com.lar.customeronboarding.dto.response.LoginResponse;
import com.lar.customeronboarding.exception.error.ApiError;
import com.lar.customeronboarding.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Customer login")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Log in",
            description = "Authenticates with username and the password issued at registration; returns a JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated, token returned"),
            @ApiResponse(responseCode = "400", description = "Missing fields",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return new LoginResponse(authService.login(request.username(), request.password()));
    }
}
