package com.lar.customeronboarding.support.testdata;

import com.lar.customeronboarding.entity.Account;
import com.lar.customeronboarding.enums.AccountType;
import com.lar.customeronboarding.enums.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public final class AccountTestDataProvider {

    public static final String IBAN = "NL91RBNK0417164300";

    public static Account.AccountBuilder accountBuilder(UUID customerId) {
        return Account.builder()
                .customerId(customerId)
                .iban(IBAN)
                .accountType(AccountType.CURRENT)
                .balance(BigDecimal.ZERO)
                .currency(Currency.EUR);
    }

}
