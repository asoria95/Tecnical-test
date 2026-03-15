package com.challenge.account.application;

import com.challenge.account.domain.model.Account;
import com.challenge.account.infrastructure.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountRepository accountRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountService(accountRepository);
    }

    @Test
    void shouldCreateAccountWithCorrectInitialBalance() {
        CreateAccountRequest request = new CreateAccountRequest(
                "478758", "Ahorros", new BigDecimal("1000.00"), "1"
        );

        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(1L);
            return account;
        });

        AccountResponse response = accountService.createAccount(request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());

        Account saved = captor.getValue();
        assertThat(saved.getAccountNumber()).isEqualTo("478758");
        assertThat(saved.getAccountType()).isEqualTo("Ahorros");
        assertThat(saved.getInitialBalance()).isEqualByComparingTo("1000.00");
        assertThat(saved.getBalance()).isEqualByComparingTo("1000.00");
        assertThat(saved.getStatus()).isTrue();
        assertThat(saved.getCustomerId()).isEqualTo("1");

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.balance()).isEqualByComparingTo("1000.00");
    }
}
