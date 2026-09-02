package com.lar.customeronboarding.repository;

import com.lar.customeronboarding.support.integrationtest.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountNumberRepositoryIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private AccountNumberRepository accountNumberRepository;

    @Test
    void returnsPositiveNumbersWithinIbanRange() {
        var number = accountNumberRepository.nextAccountNumber();

        assertAll(
                () -> assertTrue(number > 0),
                () -> assertTrue(number <= 9_999_999_999L)
        );
    }

    @Test
    void returnsIncreasingNumbers() {
        var first = accountNumberRepository.nextAccountNumber();
        var second = accountNumberRepository.nextAccountNumber();

        assertTrue(second > first);
    }

}
