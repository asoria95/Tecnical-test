package com.challenge.account.domain.exception;

/**
 * Thrown when the customer-service reports that a customer does not exist (e.g. HTTP 404),
 * or when the customer id format is invalid.
 */
public class CustomerNotFoundException extends BusinessException {

    public CustomerNotFoundException(Long customerId) {
        super("Customer not found with id: " + customerId);
    }

    public CustomerNotFoundException(String message) {
        super(message);
    }
}
