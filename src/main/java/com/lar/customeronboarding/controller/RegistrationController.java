package com.lar.customeronboarding.controller;

import com.lar.customeronboarding.service.CustomerRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
public class RegistrationController {

    private final CustomerRegistrationService customerRegistrationService;


}
