package com.challenge.customer.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Test
    void shouldDefaultStatusToActiveWhenStatusIsNull() {
        Customer customer = new Customer(
                "John Doe",
                "M",
                30,
                "12345678",
                "Calle 1",
                "555-0000",
                "secret",
                null
        );

        assertThat(customer.getStatus()).isEqualTo("ACTIVE");
    }

}
