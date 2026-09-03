package com.lar.customeronboarding.exception.custom;

public class CountryNotAllowedException extends RuntimeException {

    public CountryNotAllowedException(String countryCode) {
        super("Registration is not available for country: " + countryCode);
    }
}
