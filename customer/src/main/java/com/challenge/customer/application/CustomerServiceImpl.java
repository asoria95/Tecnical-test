package com.challenge.customer.application;

import com.challenge.customer.domain.Customer;
import com.challenge.customer.infrastructure.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public Customer create(CreateCustomerCommand command) {
        Customer customer = new Customer(command.name(), command.identification(), "ACTIVE");
        return customerRepository.save(customer);
    }
}
