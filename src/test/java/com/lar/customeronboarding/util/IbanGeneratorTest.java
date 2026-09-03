package com.lar.customeronboarding.util;

import com.lar.customeronboarding.repository.AccountNumberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IbanGeneratorTest {

    @Mock
    private AccountNumberRepository accountNumberRepository;

    @InjectMocks
    private IbanGenerator ibanGenerator;

    @Test
    void generatesIbanWithExpectedStructure() {
        when(accountNumberRepository.nextAccountNumber()).thenReturn(417_164_300L);

        var iban = ibanGenerator.generate();

        assertAll(
                () -> assertEquals(18, iban.length()),
                () -> assertTrue(iban.matches("^NL\\d{2}RBNK\\d{10}$")),
                () -> assertTrue(iban.endsWith("0417164300"))
        );
    }

    @Test
    void padsAccountNumberToTenDigits() {
        when(accountNumberRepository.nextAccountNumber()).thenReturn(1L);

        var iban = ibanGenerator.generate();

        assertTrue(iban.endsWith("RBNK0000000001"));
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 42L, 417_164_300L, 9_999_999_999L})
    void generatedIbanPassesMod97Validation(long accountNumber) {
        when(accountNumberRepository.nextAccountNumber()).thenReturn(accountNumber);

        var iban = ibanGenerator.generate();

        assertEquals(1, mod97(iban));
    }

    @Test
    void generatesDistinctIbansForDistinctAccountNumbers() {
        when(accountNumberRepository.nextAccountNumber()).thenReturn(1L, 2L);

        var first = ibanGenerator.generate();
        var second = ibanGenerator.generate();

        assertNotEquals(first, second);
    }

    private static int mod97(String iban) {
        var rearranged = iban.substring(4) + iban.substring(0, 4);
        var numeric = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            numeric.append(Character.isDigit(c) ? String.valueOf(c) : String.valueOf(c - 'A' + 10));
        }
        return new BigInteger(numeric.toString()).mod(BigInteger.valueOf(97)).intValue();
    }

}
