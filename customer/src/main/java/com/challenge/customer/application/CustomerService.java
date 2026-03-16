package com.challenge.customer.application;

import com.challenge.customer.domain.Customer;

public interface CustomerService {

    Customer create(CreateCustomerCommand command);
}
