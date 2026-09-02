package com.lar.customeronboarding.mapper;

import com.lar.customeronboarding.dto.AddressDto;
import com.lar.customeronboarding.dto.CustomerDto;
import com.lar.customeronboarding.dto.request.AddressRequest;
import com.lar.customeronboarding.dto.request.RegisterCustomerRequest;
import com.lar.customeronboarding.dto.response.RegisterCustomerResponse;
import com.lar.customeronboarding.entity.Address;
import com.lar.customeronboarding.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CustomerMapper {

    CustomerDto toDto(RegisterCustomerRequest request, String password);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Customer toEntity(CustomerDto dto);

    @Mapping(target = "customerId", source = "id")
    RegisterCustomerResponse toResponse(Customer customer);

    AddressDto toAddressDto(AddressRequest request);

    Address toAddress(AddressDto dto);
}