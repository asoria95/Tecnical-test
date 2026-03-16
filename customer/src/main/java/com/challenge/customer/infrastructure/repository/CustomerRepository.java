package com.challenge.customer.infrastructure.repository;

import com.challenge.customer.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByIdentification(String identification);
}
