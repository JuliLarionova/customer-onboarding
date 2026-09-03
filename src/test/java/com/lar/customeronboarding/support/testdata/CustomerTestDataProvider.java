package com.lar.customeronboarding.support.testdata;

import com.lar.customeronboarding.dto.AddressDto;
import com.lar.customeronboarding.dto.CustomerDto;
import com.lar.customeronboarding.dto.RegisteredCustomerDto;
import com.lar.customeronboarding.dto.request.AddressRequest;
import com.lar.customeronboarding.dto.request.RegisterCustomerRequest;
import com.lar.customeronboarding.entity.Address;
import com.lar.customeronboarding.entity.Customer;

import java.time.LocalDate;
import java.util.UUID;

public final class CustomerTestDataProvider {

    public static final String JWT_TEST_SECRET = "0123456789abcdef0123456789abcdef";

    public static final UUID CUSTOMER_ID = UUID.fromString("6f1c2a4e-9b3d-4c5a-8e7f-1a2b3c4d5e6f");
    public static final String USERNAME = "JSmith";
    public static final String FIRST_NAME = "Jane";
    public static final String LAST_NAME = "Smith";
    public static final String PASSWORD = "generated";
    public static final String STREET = "Appelstraat";
    public static final String HOUSE_NUMBER = "159A";
    public static final String POSTAL_CODE = "1131AB";
    public static final String CITY = "Amsterdam";
    public static final String COUNTRY_CODE = "NL";
    public static final LocalDate DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);

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
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .dateOfBirth(DATE_OF_BIRTH)
                .address(addressDtoBuilder().build());
    }

    public static RegisteredCustomerDto.RegisteredCustomerDtoBuilder registeredCustomerDtoBuilder() {
        return RegisteredCustomerDto.builder()
                .id(CUSTOMER_ID)
                .username(USERNAME)
                .password(PASSWORD);
    }

}
