package com.challenge.customer.application;

import com.challenge.customer.domain.Customer;

import java.util.List;

public interface CustomerService {

    Customer create(CreateCustomerCommand command);

    List<Customer> getAll();

    Customer getById(Long id);

    Customer findByName(String name);

    Customer update(UpdateCustomerCommand command);

    void delete(Long id);
}
