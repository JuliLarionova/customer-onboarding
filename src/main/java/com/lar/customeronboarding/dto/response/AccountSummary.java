package com.lar.customeronboarding.dto.response;

import com.lar.customeronboarding.enums.AccountType;
import com.lar.customeronboarding.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Schema(description = "Summary of one account")
public record AccountSummary(

        @Schema(description = "IBAN of the account", example = "NL91RBNK0417164300")
        String iban,

        @Schema(description = "Account type", example = "CURRENT")
        AccountType accountType,

        @Schema(description = "Current balance", example = "0.0000")
        BigDecimal balance,

        @Schema(description = "Account currency", example = "EUR")
        Currency currency
) {}
