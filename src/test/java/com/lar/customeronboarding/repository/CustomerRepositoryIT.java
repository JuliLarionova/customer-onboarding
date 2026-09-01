package com.lar.customeronboarding.repository;

import com.lar.customeronboarding.entity.Address;
import com.lar.customeronboarding.entity.Customer;
import com.lar.customeronboarding.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;

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
        var saved = customerRepository.saveAndFlush(buildCustomer("jsmith"));

        assertAll(
                () -> assertNotNull(saved.getId()),
                () -> assertNotNull(saved.getCreatedAt()),
                () -> assertNotNull(saved.getUpdatedAt())
        );
    }

    @Test
    void rejectsDuplicateUsername() {
        customerRepository.saveAndFlush(buildCustomer("taken"));

        assertThatThrownBy(() -> customerRepository.saveAndFlush(buildCustomer("taken")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void persistedCustomerRoundTripsAllFields() {
        var saved = customerRepository.saveAndFlush(buildCustomer("jsmith"));
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

    private Customer buildCustomer(String username) {
        return Customer.builder()
                .username(username)
                .password("generated")
                .firstName("Jane")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address(Address.builder()
                        .street("Damrak")
                        .houseNumber("1")
                        .postalCode("1012LG")
                        .city("Amsterdam")
                        .countryCode("NL")
                        .build())
                .build();
    }

}
