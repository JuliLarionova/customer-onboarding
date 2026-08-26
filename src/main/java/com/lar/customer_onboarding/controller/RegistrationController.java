package com.lar.customer_onboarding.controller;

import com.lar.customer_onboarding.service.CustomerRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
public class RegistrationController {

    private final CustomerRegistrationService customerRegistrationService;


}
