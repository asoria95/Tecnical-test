package com.challenge.customer.application;

public record UpdateCustomerCommand(
        Long customerId,
        String name,
        String gender,
        Integer age,
        String identification,
        String address,
        String phone,
        String password,
        String status
) {
}
