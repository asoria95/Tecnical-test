package com.challenge.customer.api;

public record CustomerResponse(
        Long id,
        String name,
        String gender,
        Integer age,
        String identification,
        String address,
        String phone,
        String status
) {
}
