package com.lar.customeronboarding.controller;

import com.lar.customeronboarding.dto.request.RegisterCustomerRequest;
import com.lar.customeronboarding.dto.response.RegisterCustomerResponse;
import com.lar.customeronboarding.exception.error.ApiError;
import com.lar.customeronboarding.mapper.CustomerMapper;
import com.lar.customeronboarding.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Registration", description = "Customer registration and account opening")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final CustomerMapper customerMapper;

    @Operation(summary = "Register a customer",
            description = "Registers a customer with personal details and address, generates a password, "
                    + "and opens a CURRENT account with a Dutch IBAN. Only customers from allowed "
                    + "countries (NL, BE) aged 18 or older may register.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer registered, credentials returned"),
            @ApiResponse(responseCode = "400", description = "Validation failed (missing fields, underage, malformed input)",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Username already taken",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Country not allowed",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegisterCustomerResponse> register(
            @Valid @RequestBody RegisterCustomerRequest request) {

        var registered = registrationService.register(customerMapper.toDto(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerMapper.toResponse(registered));
    }

}
