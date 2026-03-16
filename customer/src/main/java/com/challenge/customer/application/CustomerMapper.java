package com.challenge.customer.application;

import com.challenge.customer.api.CustomerResponse;
import com.challenge.customer.domain.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getIdentification(),
                customer.getStatus()
        );
    }
}
