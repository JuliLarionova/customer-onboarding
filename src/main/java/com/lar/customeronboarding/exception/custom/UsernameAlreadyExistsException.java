package com.lar.customeronboarding.exception.custom;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String username) {
        super("Account with this username already exists: " + username);
    }
}
