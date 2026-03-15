package com.challenge.account.infrastructure.repository;

import com.challenge.account.domain.model.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovementRepository extends JpaRepository<Movement, Long> {

    List<Movement> findByAccountId(Long accountId);

    List<Movement> findByAccountCustomerIdAndDateBetween(String customerId, LocalDateTime from, LocalDateTime to);
}
