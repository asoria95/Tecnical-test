package com.challenge.account.application;

import com.challenge.account.domain.exception.DuplicateAccountException;
import com.challenge.account.domain.model.Account;
import com.challenge.account.infrastructure.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResponse createAccount(CreateAccountRequest request) {

        validateAccountNumber(request.accountNumber());

        Account account = new Account(
                request.accountNumber(),
                request.accountType(),
                request.initialBalance(),
                request.customerId()
        );

        Account saved = accountRepository.save(account);

        return new AccountResponse(
            saved.getId(),
            saved.getAccountNumber(),
            saved.getAccountType(),
            saved.getInitialBalance(),
            saved.getBalance(),
            saved.getStatus(),
            saved.getCustomerId()
        );
    }

    private void validateAccountNumber(String accountNumber) {
        accountRepository.findByAccountNumber(accountNumber).ifPresent(existing -> {
            throw new DuplicateAccountException(accountNumber);
        });
    }
}
