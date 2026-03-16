package com.challenge.customer.domain.exception;

public class CustomerNotFoundException extends BusinessException {

    public CustomerNotFoundException(Long customerId) {
        super("Customer not found: " + customerId);
    }
}
