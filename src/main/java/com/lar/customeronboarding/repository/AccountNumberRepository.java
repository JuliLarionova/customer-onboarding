package com.lar.customeronboarding.repository;

import com.lar.customeronboarding.entity.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.UUID;

public interface AccountNumberRepository extends Repository<Account, UUID> {

    @Query(value = "SELECT nextval('account_number_seq')", nativeQuery = true)
    long nextAccountNumber();

}
