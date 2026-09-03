package com.lar.customeronboarding.controller;


import com.lar.customeronboarding.dto.request.RegisterCustomerRequest;
import com.lar.customeronboarding.dto.response.RegisterCustomerResponse;
import com.lar.customeronboarding.mapper.CustomerMapper;
import com.lar.customeronboarding.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final CustomerMapper customerMapper;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegisterCustomerResponse> register(
            @Valid @RequestBody RegisterCustomerRequest request) {

        var registered = registrationService.register(customerMapper.toDto(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerMapper.toResponse(registered));
    }

}
