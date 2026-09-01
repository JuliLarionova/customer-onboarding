package com.lar.customeronboarding.repository;

import com.lar.customeronboarding.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence access for {@link Customer}.
 */
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByUsername(String username);

}
