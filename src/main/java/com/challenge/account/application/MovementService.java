package com.challenge.account.application;

import com.challenge.account.domain.exception.AccountNotFoundException;
import com.challenge.account.domain.model.Account;
import com.challenge.account.domain.model.Movement;
import com.challenge.account.infrastructure.repository.AccountRepository;
import com.challenge.account.infrastructure.repository.MovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class MovementService {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;

    @Autowired
    public MovementService(AccountRepository accountRepository, MovementRepository movementRepository) {
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
    }

    @Transactional
    public MovementResponse registerDeposit(RegisterMovementRequest request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new AccountNotFoundException(request.accountId()));

        BigDecimal newBalance = account.getBalance().add(request.amount());
        account.setBalance(newBalance);
        accountRepository.save(account);

        Movement movement = new Movement("Deposito", request.amount(), newBalance, account);
        Movement saved = movementRepository.save(movement);

        return new MovementResponse(
                saved.getId(),
                saved.getDate(),
                saved.getMovementType(),
                saved.getAmount(),
                saved.getBalance()
        );
    }
}
