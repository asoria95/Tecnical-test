package com.challenge.customer.application;

public record UpdateCustomerCommand(Long customerId, String name, String identification) {
}
