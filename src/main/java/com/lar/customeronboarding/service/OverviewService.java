package com.lar.customeronboarding.service;

import com.lar.customeronboarding.dto.response.OverviewResponse;
import com.lar.customeronboarding.mapper.AccountMapper;
import com.lar.customeronboarding.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OverviewService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Transactional(readOnly = true)
    public OverviewResponse overviewFor(UUID customerId) {
        var accounts = accountRepository.findByCustomerId(customerId);
        return new OverviewResponse(accountMapper.toSummaries(accounts));
    }
}
