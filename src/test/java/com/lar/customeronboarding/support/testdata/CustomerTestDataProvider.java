package com.lar.customeronboarding.support.testdata;

import com.lar.customeronboarding.dto.AddressDto;
import com.lar.customeronboarding.dto.CustomerDto;
import com.lar.customeronboarding.dto.request.AddressRequest;
import com.lar.customeronboarding.dto.request.RegisterCustomerRequest;
import com.lar.customeronboarding.entity.Address;
import com.lar.customeronboarding.entity.Customer;

import java.time.LocalDate;

public final class CustomerTestDataProvider {

    private static final String USERNAME = "JSmith";
    private static final String FIRST_NAME = "Jane";
    private static final String LAST_NAME = "Smith";
    private static final String PASSWORD = "generated";
    private static final String STREET = "Appelstraat";
    private static final String HOUSE_NUMBER = "159A";
    private static final String POSTAL_CODE = "1131AB";
    private static final String CITY = "Amsterdam";
    private static final String COUNTRY_CODE = "NL";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);

    private CustomerTestDataProvider() {
    }

    public static Address.AddressBuilder addressBuilder() {
        return Address.builder()
                .street(STREET)
                .houseNumber(HOUSE_NUMBER)
                .postalCode(POSTAL_CODE)
                .city(CITY)
                .countryCode(COUNTRY_CODE);
    }

    public static Customer.CustomerBuilder customerBuilder() {
        return Customer.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .dateOfBirth(DATE_OF_BIRTH)
                .address(addressBuilder().build());
    }

    public static AddressRequest.AddressRequestBuilder addressRequestBuilder() {
        return AddressRequest.builder()
                .street(STREET)
                .houseNumber(HOUSE_NUMBER)
                .postalCode(POSTAL_CODE)
                .city(CITY)
                .countryCode(COUNTRY_CODE);
    }

    public static RegisterCustomerRequest.RegisterCustomerRequestBuilder registerRequestBuilder() {
        return RegisterCustomerRequest.builder()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .dateOfBirth(DATE_OF_BIRTH)
                .address(addressRequestBuilder().build());
    }

    public static AddressDto.AddressDtoBuilder addressDtoBuilder() {
        return AddressDto.builder()
                .street(STREET)
                .houseNumber(HOUSE_NUMBER)
                .postalCode(POSTAL_CODE)
                .city(CITY)
                .countryCode(COUNTRY_CODE);
    }

    public static CustomerDto.CustomerDtoBuilder customerDtoBuilder() {
        return CustomerDto.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .dateOfBirth(DATE_OF_BIRTH)
                .address(addressDtoBuilder().build());
    }

}
