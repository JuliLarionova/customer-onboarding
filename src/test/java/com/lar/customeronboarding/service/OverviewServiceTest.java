package com.lar.customeronboarding.service;

import com.lar.customeronboarding.dto.response.AccountSummary;
import com.lar.customeronboarding.enums.AccountType;
import com.lar.customeronboarding.enums.Currency;
import com.lar.customeronboarding.mapper.AccountMapper;
import com.lar.customeronboarding.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static com.lar.customeronboarding.support.testdata.AccountTestDataProvider.IBAN;
import static com.lar.customeronboarding.support.testdata.AccountTestDataProvider.accountBuilder;
import static com.lar.customeronboarding.support.testdata.CustomerTestDataProvider.CUSTOMER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OverviewServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private OverviewService overviewService;

    @Test
    void returnsMappedAccountsForCustomer() {
        var accounts = List.of(accountBuilder(CUSTOMER_ID).build());
        var summaries = List.of(new AccountSummary(IBAN, AccountType.CURRENT, BigDecimal.ZERO, Currency.EUR));
        when(accountRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(accounts);
        when(accountMapper.toSummaries(accounts)).thenReturn(summaries);

        var response = overviewService.overviewFor(CUSTOMER_ID);

        assertEquals(summaries, response.accounts());
    }

    @Test
    void returnsEmptyOverviewWhenCustomerHasNoAccounts() {
        when(accountRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(List.of());
        when(accountMapper.toSummaries(List.of())).thenReturn(List.of());

        var response = overviewService.overviewFor(CUSTOMER_ID);

        assertTrue(response.accounts().isEmpty());
    }

}
