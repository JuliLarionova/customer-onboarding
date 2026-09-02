package com.lar.customeronboarding.repository;

import com.lar.customeronboarding.enums.AccountType;
import com.lar.customeronboarding.enums.Currency;
import com.lar.customeronboarding.support.integrationtest.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.UUID;

import static com.lar.customeronboarding.support.testdata.AccountTestDataProvider.accountBuilder;
import static com.lar.customeronboarding.support.testdata.CustomerTestDataProvider.customerBuilder;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountRepositoryIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private UUID customerId;

    @BeforeEach
    void setup() {
        customerId = customerRepository.saveAndFlush(customerBuilder().build()).getId();
    }

    @Test
    void repositorySavesAccount() {
        var saved = accountRepository.saveAndFlush(accountBuilder(customerId).build());
        entityManager.clear();

        var found = accountRepository.findById(saved.getId()).orElseThrow();

        assertAll(
                () -> assertEquals(saved.getCustomerId(), found.getCustomerId()),
                () -> assertEquals(saved.getIban(), found.getIban()),
                () -> assertEquals(saved.getAccountType(), found.getAccountType()),
                () -> assertEquals(saved.getCurrency(), found.getCurrency()),
                () -> assertEquals(0, found.getBalance().compareTo(BigDecimal.ZERO))
        );
    }

    @Test
    void findsAccountsByCustomerId() {
        accountRepository.saveAndFlush(accountBuilder(customerId).build());

        var accounts = accountRepository.findByCustomerId(customerId);

        assertEquals(1, accounts.size());
    }

    @Test
    void returnsEmptyListForUnknownCustomer() {
        assertTrue(accountRepository.findByCustomerId(UUID.randomUUID()).isEmpty());
    }

    @Test
    void rejectsDuplicateIban() {
        accountRepository.saveAndFlush(accountBuilder(customerId).build());

        assertThatThrownBy(() ->
                accountRepository.saveAndFlush(accountBuilder(customerId).build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void rejectsAccountForNonexistentCustomer() {
        var invalidAccount = accountBuilder(UUID.randomUUID()).build();

        assertThatThrownBy(() -> accountRepository.saveAndFlush(invalidAccount))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}
