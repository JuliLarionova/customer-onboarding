package com.lar.customeronboarding.service;

import com.lar.customeronboarding.config.RegistrationProperties;
import com.lar.customeronboarding.dto.CustomerDto;
import com.lar.customeronboarding.dto.RegisteredCustomerDto;
import com.lar.customeronboarding.entity.Account;
import com.lar.customeronboarding.entity.Customer;
import com.lar.customeronboarding.enums.AccountType;
import com.lar.customeronboarding.enums.Currency;
import com.lar.customeronboarding.exception.custom.CountryNotAllowedException;
import com.lar.customeronboarding.exception.custom.UsernameAlreadyExistsException;
import com.lar.customeronboarding.mapper.CustomerMapper;
import com.lar.customeronboarding.repository.AccountRepository;
import com.lar.customeronboarding.repository.CustomerRepository;
import com.lar.customeronboarding.util.IbanGenerator;
import com.lar.customeronboarding.util.PasswordGenerator;
import org.hibernate.exception.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RegistrationService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final IbanGenerator ibanGenerator;
    private final PasswordGenerator passwordGenerator;
    private final CustomerMapper customerMapper;
    private final RegistrationProperties properties;

    @Transactional
    public RegisteredCustomerDto register(CustomerDto dto) {

        validateCountry(dto.address().countryCode());

        var customer = customerMapper.toEntity(dto, passwordGenerator.generate());

        saveNewCustomer(customer, dto.username());

        accountRepository.save(newCurrentAccountFor(customer.getId()));

        return customerMapper.toRegisteredDto(customer);
    }

    private void saveNewCustomer(Customer customer, String username) {
        try {
            customerRepository.saveAndFlush(customer);
        } catch (DataIntegrityViolationException e) {
            if (isUsernameConstraint(e)) {
                throw new UsernameAlreadyExistsException(username);
            }
            throw e;
        }
    }

    private static boolean isUsernameConstraint(DataIntegrityViolationException e) {
        return e.getCause() instanceof ConstraintViolationException cve
                && "uq_customer_username".equals(cve.getConstraintName());
    }

    private Account newCurrentAccountFor(UUID customerId) {
        return Account.builder()
                .customerId(customerId)
                .iban(ibanGenerator.generate())
                .accountType(AccountType.CURRENT)
                .balance(BigDecimal.ZERO)
                .currency(Currency.EUR)
                .build();
    }

    private void validateCountry(String countryCode) {
        if (!properties.allowedCountries().contains(countryCode)) {
            throw new CountryNotAllowedException(countryCode);
        }
    }

}
