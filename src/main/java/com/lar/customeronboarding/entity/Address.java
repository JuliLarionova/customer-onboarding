package com.lar.customeronboarding.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Column(name = "street", nullable = false, length = 150)
    private String street;

    @Column(name = "house_number", nullable = false, length = 20)
    private String houseNumber;

    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

}
