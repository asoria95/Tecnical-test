package com.challenge.customer.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateCustomerRequest(
        @NotBlank String name,
        String gender,
        @Min(0) @Max(150) Integer age,
        @NotBlank String identification,
        String address,
        String phone,
        String password,
        String status
) {
}
