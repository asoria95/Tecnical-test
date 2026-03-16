package com.challenge.customer.application;

import com.challenge.customer.domain.Customer;
import com.challenge.customer.domain.exception.CustomerNotFoundException;
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
        Customer customer = new Customer(
                command.name(),
                command.gender(),
                command.age(),
                command.identification(),
                command.address(),
                command.phone(),
                command.password(),
                "ACTIVE"
        );
        return customerRepository.save(customer);
    }

    @Override
    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Override
    @Transactional
    public Customer update(UpdateCustomerCommand command) {
        Customer existing = customerRepository.findById(command.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(command.customerId()));
        existing.setName(command.name());
        existing.setGender(command.gender());
        existing.setAge(command.age());
        existing.setIdentification(command.identification());
        existing.setAddress(command.address());
        existing.setPhone(command.phone());
        if (command.password() != null && !command.password().isBlank()) {
            existing.setPassword(command.password());
        }
        if (command.status() != null && !command.status().isBlank()) {
            existing.setStatus(command.status());
        }
        return customerRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customerRepository.deleteById(id);
    }
}
