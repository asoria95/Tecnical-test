package com.challenge.account.domain.exception;

public class CustomerNotFoundException extends BusinessException {

    public CustomerNotFoundException(Long customerId) {
        super("Customer not found with id: " + customerId);
    }

    public CustomerNotFoundException(String message) {
        super(message);
    }
}
