package com.lar.customeronboarding.mapper;

import com.lar.customeronboarding.enums.AccountType;
import com.lar.customeronboarding.enums.Currency;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.lar.customeronboarding.support.testdata.AccountTestDataProvider.IBAN;
import static com.lar.customeronboarding.support.testdata.AccountTestDataProvider.accountBuilder;
import static com.lar.customeronboarding.support.testdata.CustomerTestDataProvider.CUSTOMER_ID;
import static org.junit.jupiter.api.Assertions.*;

class AccountMapperTest {

    private final AccountMapper mapper = new AccountMapperImpl();

    @Test
    void mapsAccountToSummary() {
        var account = accountBuilder(CUSTOMER_ID).build();

        var summary = mapper.toSummary(account);

        assertAll(
                () -> assertEquals(IBAN, summary.iban()),
                () -> assertEquals(AccountType.CURRENT, summary.accountType()),
                () -> assertEquals(0, summary.balance().compareTo(BigDecimal.ZERO)),
                () -> assertEquals(Currency.EUR, summary.currency())
        );
    }

    @Test
    void mapsListPreservingOrder() {
        var first = accountBuilder(CUSTOMER_ID).build();
        var second = accountBuilder(CUSTOMER_ID).iban("NL02RBNK0000000002").build();

        var summaries = mapper.toSummaries(List.of(first, second));

        assertAll(
                () -> assertEquals(2, summaries.size()),
                () -> assertEquals(IBAN, summaries.get(0).iban()),
                () -> assertEquals("NL02RBNK0000000002", summaries.get(1).iban())
        );
    }

    @Test
    void returnsNullForNullInput() {
        assertAll(
                () -> assertNull(mapper.toSummary(null)),
                () -> assertNull(mapper.toSummaries(null))
        );
    }

}
