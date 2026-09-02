package com.lar.customeronboarding.repository;

import com.lar.customeronboarding.support.integrationtest.AbstractPostgresIntegrationTest;
import com.lar.customeronboarding.support.testdata.CustomerTestDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CustomerRepositoryIT extends AbstractPostgresIntegrationTest {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void savesAndAssignsGeneratedFields() {
        var saved = customerRepository.saveAndFlush(
                CustomerTestDataProvider.customerBuilder()
                        .username("jsmith")
                        .build());

        assertAll(
                () -> assertNotNull(saved.getId()),
                () -> assertNotNull(saved.getCreatedAt()),
                () -> assertNotNull(saved.getUpdatedAt())
        );
    }

    @Test
    void rejectsDuplicateUsername() {
        customerRepository.saveAndFlush(
                CustomerTestDataProvider.customerBuilder()
                        .username("taken")
                        .build());

        assertThatThrownBy(() -> customerRepository.saveAndFlush(
                CustomerTestDataProvider.customerBuilder()
                        .username("taken")
                        .build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void persistedCustomerRoundTripsAllFields() {
        var saved = customerRepository.saveAndFlush(
                CustomerTestDataProvider.customerBuilder()
                        .username("taken")
                        .build());

        entityManager.clear();

        var found = customerRepository.findById(saved.getId()).orElseThrow();

        var expected = saved.getAddress();
        var actual = found.getAddress();

        assertAll(
                () -> assertEquals(expected.getStreet(), actual.getStreet()),
                () -> assertEquals(expected.getPostalCode(), actual.getPostalCode()),
                () -> assertEquals(expected.getHouseNumber(), actual.getHouseNumber()),
                () -> assertEquals(expected.getCity(), actual.getCity()),
                () -> assertEquals(expected.getCountryCode(), actual.getCountryCode())
        );
    }

}
