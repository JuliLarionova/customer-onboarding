package com.lar.customeronboarding.util;

import com.lar.customeronboarding.repository.AccountNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@RequiredArgsConstructor
@Component
public class IbanGenerator {

    private static final String COUNTRY_CODE = "NL";
    private static final String BANK_CODE = "RBNK";
    private static final BigInteger MOD_97 = BigInteger.valueOf(97);

    private final AccountNumberRepository accountNumberRepository;

    public String generate() {
        var accountNumber = String.format("%010d", accountNumberRepository.nextAccountNumber());
        var bban = BANK_CODE + accountNumber;
        return COUNTRY_CODE + checkDigits(bban) + bban;
    }

    private static String checkDigits(String bban) {
        var numeric = toNumeric(bban + COUNTRY_CODE + "00");
        var remainder = new BigInteger(numeric).mod(MOD_97).intValue();
        return String.format("%02d", 98 - remainder);
    }

    private static String toNumeric(String input) {
        var sb = new StringBuilder(input.length() * 2);
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
            } else {
                sb.append(c - 'A' + 10);
            }
        }
        return sb.toString();
    }
}
