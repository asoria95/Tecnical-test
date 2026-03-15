package com.challenge.account.application;

import com.challenge.account.domain.exception.AccountNotFoundException;
import com.challenge.account.domain.exception.InsufficientBalanceException;
import com.challenge.account.domain.exception.InvalidAmountException;
import com.challenge.account.domain.exception.MovementNotFoundException;
import com.challenge.account.domain.model.Account;
import com.challenge.account.domain.model.Movement;
import com.challenge.account.infrastructure.repository.AccountRepository;
import com.challenge.account.infrastructure.repository.MovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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

        return toResponse(saved);
    }

    @Transactional
    public MovementResponse registerWithdrawal(RegisterMovementRequest request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new AccountNotFoundException(request.accountId()));

        if (account.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException();
        }

        BigDecimal negativeAmount = request.amount().negate();
        BigDecimal newBalance = account.getBalance().add(negativeAmount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        Movement movement = new Movement("Retiro", negativeAmount, newBalance, account);
        Movement saved = movementRepository.save(movement);

        return toResponse(saved);
    }

    @Transactional
    public MovementResponse registerMovement(Long accountId, BigDecimal signedAmount) {
        if (signedAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw new InvalidAmountException("El monto no puede ser cero");
        }
        RegisterMovementRequest request = new RegisterMovementRequest(accountId, signedAmount.abs());
        return signedAmount.signum() > 0 ? registerDeposit(request) : registerWithdrawal(request);
    }

    public MovementResponse findById(Long id) {
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new MovementNotFoundException(id));
        return toResponse(movement);
    }

    public List<MovementResponse> findByAccountId(Long accountId) {
        return movementRepository.findByAccountId(accountId).stream()
                .map(this::toResponse)
                .toList();
    }

    private MovementResponse toResponse(Movement movement) {
        return new MovementResponse(
                movement.getId(),
                movement.getDate(),
                movement.getMovementType(),
                movement.getAmount(),
                movement.getBalance()
        );
    }
}
