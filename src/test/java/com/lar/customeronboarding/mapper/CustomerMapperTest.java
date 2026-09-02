package com.lar.customeronboarding.mapper;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.lar.customeronboarding.support.testdata.CustomerTestDataProvider.*;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapperImpl();

    private static final String PASSWORD = "PASSWORD";

    @Test
    void mapsRequestAndPasswordToDto() {
        var request = registerRequestBuilder().build();

        var dto = mapper.toDto(request, PASSWORD);

        assertAll(
                () -> assertEquals(request.username(), dto.username()),
                () -> assertEquals(PASSWORD, dto.password()),
                () -> assertEquals(request.firstName(), dto.firstName()),
                () -> assertEquals(request.lastName(), dto.lastName()),
                () -> assertEquals(request.dateOfBirth(), dto.dateOfBirth())
        );
    }

    @Test
    void mapsNestedAddressToDto() {
        var request = registerRequestBuilder().build();
        var dto = mapper.toDto(request, PASSWORD);

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

        var customer = mapper.toEntity(dto);

        assertAll(
                () -> assertNull(customer.getId()),
                () -> assertNull(customer.getCreatedAt()),
                () -> assertNull(customer.getUpdatedAt()),
                () -> assertEquals(dto.username(), customer.getUsername()),
                () -> assertEquals(dto.password(), customer.getPassword()),
                () -> assertEquals(dto.firstName(), customer.getFirstName()),
                () -> assertEquals(dto.lastName(), customer.getLastName()),
                () -> assertEquals(dto.dateOfBirth(), customer.getDateOfBirth())
        );
    }

    @Test
    void mapsNestedAddressToEntity() {
        var dto = customerDtoBuilder().build();
        var customer = mapper.toEntity(dto);
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
    void mapsEntityToResponse() {
        var id = UUID.randomUUID();
        var entity = customerBuilder().id(id).build();

        var response = mapper.toResponse(entity);

        assertAll(
                () -> assertEquals(entity.getId(), response.customerId()),
                () -> assertEquals(entity.getUsername(), response.username()),
                () -> assertEquals(entity.getPassword(), response.password())
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
                () -> assertNull(mapper.toDto(null, null)),
                () -> assertNull(mapper.toEntity(null)),
                () -> assertNull(mapper.toResponse(null)),
                () -> assertNull(mapper.toAddress(null))
        );
    }
}
