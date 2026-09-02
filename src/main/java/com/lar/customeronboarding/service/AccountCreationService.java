package com.lar.customeronboarding.service;

import com.lar.customeronboarding.entity.Account;
import com.lar.customeronboarding.enums.AccountType;
import com.lar.customeronboarding.enums.Currency;
import com.lar.customeronboarding.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AccountCreationService {

    private final AccountRepository accountRepository;
    private final IbanGenerator ibanGenerator;

    @Transactional(propagation = Propagation.MANDATORY)
    public Account openFor(UUID customerId) {
        var account = Account.builder()
                .customerId(customerId)
                .iban(ibanGenerator.generate())
                .accountType(AccountType.CURRENT)
                .balance(BigDecimal.ZERO)
                .currency(Currency.EUR)
                .build();

        return accountRepository.save(account);
    }

}
