package com.lar.customeronboarding.mapper;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.lar.customeronboarding.support.testdata.CustomerTestDataProvider.*;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapperImpl();

//    private static final String PASSWORD = "PASSWORD";
//    private static final UUID CUSTOMER_ID = UUID.fromString("6f1c2a4e-9b3d-4c5a-8e7f-1a2b3c4d5e6f");

    @Test
    void mapsRequestAndPasswordToDto() {
        var request = registerRequestBuilder().build();

        var dto = mapper.toDto(request);

        assertAll(
                () -> assertEquals(request.username(), dto.username()),
                () -> assertEquals(request.firstName(), dto.firstName()),
                () -> assertEquals(request.lastName(), dto.lastName()),
                () -> assertEquals(request.dateOfBirth(), dto.dateOfBirth())
        );
    }

    @Test
    void mapsNestedAddressToDto() {
        var request = registerRequestBuilder().build();
        var dto = mapper.toDto(request);

        var address = dto.address();

        assertAll(
                () -> assertNotNull(address),
                () -> assertEquals(request.address().street(), address.street()),
                () -> assertEquals(request.address().houseNumber(), address.houseNumber()),
                () -> assertEquals(request.address().postalCode(), address.postalCode()),
                () -> assertEquals(request.address().city(), address.city()),
                () -> assertEquals(request.address().countryCode(), address.countryCode())
        );
    }

    @Test
    void mapsDtoToEntityWithoutGeneratedFields() {
        var dto = customerDtoBuilder().build();

        var customer = mapper.toEntity(dto, PASSWORD);

        assertAll(
                () -> assertNull(customer.getId()),
                () -> assertNull(customer.getCreatedAt()),
                () -> assertNull(customer.getUpdatedAt()),
                () -> assertEquals(dto.username(), customer.getUsername()),
                () -> assertEquals(PASSWORD, customer.getPassword()),
                () -> assertEquals(dto.firstName(), customer.getFirstName()),
                () -> assertEquals(dto.lastName(), customer.getLastName()),
                () -> assertEquals(dto.dateOfBirth(), customer.getDateOfBirth())
        );
    }

    @Test
    void mapsNestedAddressToEntity() {
        var dto = customerDtoBuilder().build();
        var customer = mapper.toEntity(dto, PASSWORD);
        var address = customer.getAddress();

        assertAll(
                () -> assertNotNull(address),
                () -> assertEquals(dto.address().street(), address.getStreet()),
                () -> assertEquals(dto.address().houseNumber(), address.getHouseNumber()),
                () -> assertEquals(dto.address().postalCode(), address.getPostalCode()),
                () -> assertEquals(dto.address().city(), address.getCity()),
                () -> assertEquals(dto.address().countryCode(), address.getCountryCode())
        );
    }

    @Test
    void mapsAddressRequestToAddressDto() {
        var request = addressRequestBuilder().build();
        var dto = mapper.toAddressDto(addressRequestBuilder().build());

        assertAll(
                () -> assertEquals(request.street(), dto.street()),
                () -> assertEquals(request.houseNumber(), dto.houseNumber()),
                () -> assertEquals(request.postalCode(), dto.postalCode()),
                () -> assertEquals(request.city(), dto.city()),
                () -> assertEquals(request.countryCode(), dto.countryCode())
        );
    }

    @Test
    void returnsNullForNullInput() {
        assertAll(
                () -> assertNull(mapper.toDto(null)),
                () -> assertNull(mapper.toEntity(null, null)),
                () -> assertNull(mapper.toResponse(null)),
                () -> assertNull(mapper.toAddress(null))
        );
    }

    @Test
    void mapsEntityToRegisteredDto() {
        var customer = customerBuilder().id(CUSTOMER_ID).build();

        var registered = mapper.toRegisteredDto(customer);

        assertAll(
                () -> assertEquals(customer.getId(), registered.id()),
                () -> assertEquals(customer.getUsername(), registered.username()),
                () -> assertEquals(customer.getPassword(), registered.password())
        );
    }

    @Test
    void mapsRegisteredDtoToResponse() {
        var registered = registeredCustomerDtoBuilder().build();

        var response = mapper.toResponse(registered);

        assertAll(
                () -> assertEquals(registered.id(), response.customerId()),
                () -> assertEquals(registered.username(), response.username()),
                () -> assertEquals(registered.password(), response.password())
        );
    }

}
