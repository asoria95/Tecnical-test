package com.challenge.account.service;

import com.challenge.account.application.MovementResponse;
import com.challenge.account.application.MovementService;
import com.challenge.account.application.RegisterMovementRequest;
import com.challenge.account.domain.exception.AccountNotFoundException;
import com.challenge.account.domain.model.Account;
import com.challenge.account.domain.model.Movement;
import com.challenge.account.infrastructure.repository.AccountRepository;
import com.challenge.account.infrastructure.repository.MovementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MovementRepository movementRepository;

    @InjectMocks
    private MovementService movementService;

    @Test
    void shouldRegisterDepositAndIncreaseBalance() {
        Account account = new Account("478758", "Ahorros", new BigDecimal("1000.00"), "customer-1");
        RegisterMovementRequest request = new RegisterMovementRequest(1L, new BigDecimal("500.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(movementRepository.save(any(Movement.class))).thenAnswer(inv -> inv.getArgument(0));

        MovementResponse response = movementService.registerDeposit(request);

        assertThat(response).isNotNull();
        assertThat(response.amount()).isEqualByComparingTo("500.00");
        assertThat(response.balance()).isEqualByComparingTo("1500.00");
        assertThat(response.movementType()).isEqualTo("Deposito");
    }

    @Test
    void shouldRecordMovementWithCorrectDetails() {
        Account account = new Account("478758", "Ahorros", new BigDecimal("1000.00"), "customer-1");
        RegisterMovementRequest request = new RegisterMovementRequest(1L, new BigDecimal("500.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(movementRepository.save(any(Movement.class))).thenAnswer(inv -> inv.getArgument(0));

        movementService.registerDeposit(request);

        ArgumentCaptor<Movement> movementCaptor = ArgumentCaptor.forClass(Movement.class);
        verify(movementRepository).save(movementCaptor.capture());

        Movement saved = movementCaptor.getValue();
        assertThat(saved.getMovementType()).isEqualTo("Deposito");
        assertThat(saved.getAmount()).isEqualByComparingTo("500.00");
        assertThat(saved.getBalance()).isEqualByComparingTo("1500.00");
        assertThat(saved.getAccount()).isSameAs(account);
        assertThat(saved.getDate()).isNotNull();
    }

    @Test
    void shouldPersistUpdatedAccountAfterDeposit() {
        Account account = new Account("478758", "Ahorros", new BigDecimal("1000.00"), "customer-1");
        RegisterMovementRequest request = new RegisterMovementRequest(1L, new BigDecimal("500.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(movementRepository.save(any(Movement.class))).thenAnswer(inv -> inv.getArgument(0));

        movementService.registerDeposit(request);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());

        Account persisted = accountCaptor.getValue();
        assertThat(persisted.getBalance()).isEqualByComparingTo("1500.00");
    }

    @Test
    void shouldRejectDepositWhenAccountNotFound() {
        RegisterMovementRequest request = new RegisterMovementRequest(99L, new BigDecimal("500.00"));

        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movementService.registerDeposit(request))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("99");
    }
}
