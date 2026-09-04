package com.lar.customeronboarding.mapper;

import com.lar.customeronboarding.dto.response.AccountSummary;
import com.lar.customeronboarding.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AccountMapper {

    AccountSummary toSummary(Account account);

    List<AccountSummary> toSummaries(List<Account> accounts);

}
