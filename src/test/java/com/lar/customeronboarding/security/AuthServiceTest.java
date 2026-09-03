package com.lar.customeronboarding.security;

import com.lar.customeronboarding.exception.custom.InvalidCredentialsException;
import com.lar.customeronboarding.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.lar.customeronboarding.support.testdata.CustomerTestDataProvider.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.TOKEN;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    TokenService tokenService;

    @InjectMocks
    AuthService authService;

    @Test
    void returnsTokenForValidCredentials() {
        var customer = customerBuilder().build();
        when(customerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(customer));
        when(tokenService.issueFor(customer)).thenReturn(TOKEN);

        assertEquals(TOKEN, authService.login(USERNAME, PASSWORD));
    }

    @Test
    void rejectsUnknownUsername() {
        when(customerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(USERNAME, PASSWORD));

        verifyNoInteractions(tokenService);
    }

    @Test
    void rejectsWrongPassword() {
        var customer = customerBuilder().build();
        when(customerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(customer));

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(USERNAME, "wrong-password"));

        verifyNoInteractions(tokenService);
    }

    @Test
    void failureModesAreIndistinguishable() {
        when(customerRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        when(customerRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(customerBuilder().build()));

        var unknownUser = assertThrows(InvalidCredentialsException.class,
                () -> authService.login("nouser", PASSWORD));
        var wrongPassword = assertThrows(InvalidCredentialsException.class,
                () -> authService.login(USERNAME, "wrong"));

        assertEquals(unknownUser.getMessage(), wrongPassword.getMessage());
    }

}
