package com.lar.customeronboarding.controller;

import com.lar.customeronboarding.mapper.CustomerMapper;
import com.lar.customeronboarding.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/registration")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final CustomerMapper customerMapper;


    //postmapping with registercustomerrequest as body

}
