package com.challenge.customer.api;

import jakarta.validation.constraints.NotBlank;

public record UpdateCustomerRequest(
        @NotBlank String name,
        @NotBlank String identification
) {
}
