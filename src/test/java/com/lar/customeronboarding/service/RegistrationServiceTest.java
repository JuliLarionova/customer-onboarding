package com.lar.customeronboarding.service;

import com.lar.customeronboarding.config.RegistrationProperties;
import com.lar.customeronboarding.entity.Account;
import com.lar.customeronboarding.enums.AccountType;
import com.lar.customeronboarding.enums.Currency;
import com.lar.customeronboarding.exception.custom.CountryNotAllowedException;
import com.lar.customeronboarding.exception.custom.UsernameAlreadyExistsException;
import com.lar.customeronboarding.mapper.CustomerMapper;
import com.lar.customeronboarding.repository.AccountRepository;
import com.lar.customeronboarding.repository.CustomerRepository;
import com.lar.customeronboarding.util.IbanGenerator;
import com.lar.customeronboarding.util.PasswordGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.SQLException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Set;

import static com.lar.customeronboarding.support.testdata.AccountTestDataProvider.IBAN;
import static com.lar.customeronboarding.support.testdata.CustomerTestDataProvider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private IbanGenerator ibanGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private CustomerMapper customerMapper;

    private RegistrationService service;

    @BeforeEach
    void setUp() {
        var properties = new RegistrationProperties(Set.of("NL", "BE"));
        service = new RegistrationService(customerRepository, accountRepository,
                ibanGenerator, passwordGenerator, customerMapper, properties);
    }

    @Test
    void registersCustomerAndOpensAccount() {
        var dto = customerDtoBuilder().build();
        var customer = customerBuilder().id(CUSTOMER_ID).build();
        var registered = registeredCustomerDtoBuilder().build();

        when(passwordGenerator.generate()).thenReturn(PASSWORD);
        when(customerMapper.toEntity(dto, PASSWORD)).thenReturn(customer);
        when(customerRepository.saveAndFlush(customer)).thenReturn(customer);
        when(ibanGenerator.generate()).thenReturn(IBAN);
        when(customerMapper.toRegisteredDto(customer)).thenReturn(registered);

        var result = service.register(dto);

        assertEquals(registered, result);

        var accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        var account = accountCaptor.getValue();

        assertAll(
                () -> assertEquals(customer.getId(), account.getCustomerId()),
                () -> assertEquals(IBAN, account.getIban()),
                () -> assertEquals(AccountType.CURRENT, account.getAccountType()),
                () -> assertEquals(0, account.getBalance().compareTo(BigDecimal.ZERO)),
                () -> assertEquals(Currency.EUR, account.getCurrency())
        );
    }

    @Test
    void rejectsDisallowedCountry() {
        var dto = customerDtoBuilder()
                .address(addressDtoBuilder().countryCode("DE").build())
                .build();

        assertThrows(CountryNotAllowedException.class,
                () -> service.register(dto));

        verifyNoInteractions(customerRepository, accountRepository, ibanGenerator, passwordGenerator);
    }

    @Test
    void translatesConstraintViolationToUsernameAlreadyExists() {
        var dto = customerDtoBuilder().build();
        var customer = customerBuilder().build();

        when(passwordGenerator.generate()).thenReturn(PASSWORD);
        when(customerMapper.toEntity(dto, PASSWORD)).thenReturn(customer);
        when(customerRepository.saveAndFlush(customer))
                .thenThrow(usernameConstraintViolation());

        assertThrows(UsernameAlreadyExistsException.class, () -> service.register(dto));

        verifyNoInteractions(accountRepository, ibanGenerator);
    }

    @Test
    void rethrowsUnrecognizedIntegrityViolationWithoutOpeningAccount() {
        var dto = customerDtoBuilder().build();

        when(passwordGenerator.generate()).thenReturn(PASSWORD);
        when(customerMapper.toEntity(any(), any())).thenReturn(customerBuilder().build());
        when(customerRepository.saveAndFlush(any()))
                .thenThrow(new DataIntegrityViolationException("some other constraint"));

        assertThrows(DataIntegrityViolationException.class, () -> service.register(dto));

        verify(accountRepository, never()).save(any());
    }

    private DataIntegrityViolationException usernameConstraintViolation() {
        var hibernateCause = new ConstraintViolationException(
                "could not execute statement",
                new SQLException("duplicate key value violates unique constraint"),
                "uq_customer_username");
        return new DataIntegrityViolationException("could not execute statement", hibernateCause);
    }

}
