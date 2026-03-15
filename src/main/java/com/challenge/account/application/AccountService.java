package com.challenge.account.application;

import com.challenge.account.domain.exception.AccountNotFoundException;
import com.challenge.account.domain.exception.DuplicateAccountException;
import com.challenge.account.domain.model.Account;
import com.challenge.account.infrastructure.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

        return toResponse(accountRepository.save(account));
    }

    public List<AccountResponse> findAll() {
        return accountRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public AccountResponse findById(Long id) {
        return accountRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    public AccountResponse updateAccount(Long id, UpdateAccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        if (request.accountType() != null) {
            account.setAccountType(request.accountType());
        }
        if (request.status() != null) {
            account.setStatus(request.status());
        }

        return toResponse(accountRepository.save(account));
    }

    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException(id);
        }
        accountRepository.deleteById(id);
    }

    private void validateAccountNumber(String accountNumber) {
        accountRepository.findByAccountNumber(accountNumber).ifPresent(existing -> {
            throw new DuplicateAccountException(accountNumber);
        });
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getInitialBalance(),
                account.getBalance(),
                account.getStatus(),
                account.getCustomerId()
        );
    }
}
