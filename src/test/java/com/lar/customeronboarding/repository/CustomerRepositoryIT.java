package com.lar.customeronboarding.repository;

import com.lar.customeronboarding.entity.Address;
import com.lar.customeronboarding.entity.Customer;
import com.lar.customeronboarding.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CustomerRepositoryIT extends AbstractPostgresIntegrationTest {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void savesAndAssignsGeneratedFields() {
        var saved = customerRepository.saveAndFlush(buildCustomer("jsmith"));

        assertAll(
                () -> assertNotNull(saved.getId()),
                () -> assertNotNull(saved.getCreatedAt()),
                () -> assertNotNull(saved.getUpdatedAt())
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
