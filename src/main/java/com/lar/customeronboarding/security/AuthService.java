package com.lar.customeronboarding.security;


import com.lar.customeronboarding.exception.custom.InvalidCredentialsException;
import com.lar.customeronboarding.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public String login(String username, String password) {
        return customerRepository.findByUsername(username)
                .filter(customer -> customer.getPassword().equals(password))
                .map(tokenService::issueFor)
                .orElseThrow(InvalidCredentialsException::new);
    }

}
