package com.challenge.customer.application;

import com.challenge.customer.domain.Customer;
import com.challenge.customer.infrastructure.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for customer creation (TDD Step 1 – failing tests first).
 *
 * Verifies that created customers are persisted correctly and can be retrieved by id.
 */
@SpringBootTest
@Transactional
class CustomerServiceIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldPersistCustomerWhenCreated() {
        CreateCustomerCommand command = new CreateCustomerCommand("Integration User", "ID-999");

        Customer created = customerService.create(command);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Integration User");
        assertThat(created.getIdentification()).isEqualTo("ID-999");
        assertThat(created.getStatus()).isEqualTo("ACTIVE");

        Customer persisted = customerRepository.findById(created.getId()).orElseThrow();
        assertThat(persisted.getName()).isEqualTo("Integration User");
        assertThat(persisted.getIdentification()).isEqualTo("ID-999");
        assertThat(persisted.getStatus()).isEqualTo("ACTIVE");
    }
}
