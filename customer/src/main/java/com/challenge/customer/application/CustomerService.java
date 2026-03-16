package com.challenge.customer.application;

import com.challenge.customer.domain.Customer;

public interface CustomerService {

    Customer create(CreateCustomerCommand command);

    Customer getById(Long id);

    Customer update(UpdateCustomerCommand command);

    void delete(Long id);
}
