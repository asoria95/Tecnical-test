package com.challenge.customer.domain.exception;

public class MultipleCustomersFoundException extends BusinessException {

    public MultipleCustomersFoundException(String name) {
        super("Multiple customers found with name: " + name);
    }
}
