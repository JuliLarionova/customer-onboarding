package com.lar.customeronboarding.service;


import com.lar.customeronboarding.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//@RequiredArgsConstructor
//@Service
public class AuthService {

  //  private final CustomerRepository customerRepository;

//    public LoginResponse login(LoginRequest request) {
//        Customer customer = customerRepository.findByUsername(request.username())
//            .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));
//
//        if (!customer.getPassword().equals(request.password())) {
//            throw new InvalidCredentialsException("Invalid username or password");
//        }
//
//		String token = tokenService.issueToken(customer.getUsername());
//        return new LoginResponse(customer.getUsername(), "SUCCESS", token);
//    }
}
