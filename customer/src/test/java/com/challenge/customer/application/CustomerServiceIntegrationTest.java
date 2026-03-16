package com.challenge.customer.application;

import com.challenge.customer.domain.Customer;
import com.challenge.customer.infrastructure.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CustomerServiceIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldPersistCustomerWhenCreated() {
        CreateCustomerCommand command = new CreateCustomerCommand("Integration User", null, null, "ID-999", null, null, "password");

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
