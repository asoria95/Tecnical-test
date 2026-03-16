package com.challenge.customer.application;

public record CreateCustomerCommand(
        String name,
        String gender,
        Integer age,
        String identification,
        String address,
        String phone,
        String password
) {
}
