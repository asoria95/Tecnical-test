package com.challenge.account.service;

import com.challenge.account.application.AccountMapper;
import com.challenge.account.application.AccountResponse;
import com.challenge.account.application.AccountServiceImpl;
import com.challenge.account.application.CreateAccountRequest;
import com.challenge.account.domain.exception.DuplicateAccountException;
import com.challenge.account.domain.model.Account;
import com.challenge.account.infrastructure.repository.AccountRepository;
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
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void shouldCreateAccountAndReturnCorrectResponse() {
        CreateAccountRequest request = new CreateAccountRequest(
                "478758", "Ahorros", new BigDecimal("1000.00"), "customer-1"
        );

        Account savedAccount = new Account("478758", "Ahorros", new BigDecimal("1000.00"), "customer-1");
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(accountMapper.toResponse(any(Account.class))).thenReturn(
                new AccountResponse(1L, "478758", "Ahorros", new BigDecimal("1000.00"), new BigDecimal("1000.00"), true, "customer-1")
        );

        AccountResponse response = accountService.createAccount(request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());

        Account persisted = captor.getValue();
        assertThat(persisted.getAccountNumber()).isEqualTo("478758");
        assertThat(persisted.getAccountType()).isEqualTo("Ahorros");
        assertThat(persisted.getInitialBalance()).isEqualByComparingTo("1000.00");
        assertThat(persisted.getBalance()).isEqualByComparingTo("1000.00");
        assertThat(persisted.getStatus()).isTrue();
        assertThat(persisted.getCustomerId()).isEqualTo("customer-1");

        assertThat(response).isNotNull();
        assertThat(response.accountNumber()).isEqualTo("478758");
        assertThat(response.accountType()).isEqualTo("Ahorros");
        assertThat(response.status()).isTrue();
        assertThat(response.customerId()).isEqualTo("customer-1");
    }

    @Test
    void shouldRejectAccountCreationWhenAccountNumberAlreadyExists() {
        CreateAccountRequest request = new CreateAccountRequest(
                "478758", "Ahorros", new BigDecimal("1000.00"), "customer-1"
        );

        when(accountRepository.findByAccountNumber("478758"))
                .thenReturn(Optional.of(new Account("478758", "Ahorros", new BigDecimal("500.00"), "customer-99")));

        assertThatThrownBy(() -> accountService.createAccount(request))
                .isInstanceOf(DuplicateAccountException.class);
    }
}
