package com.challenge.account.application;

import com.challenge.account.domain.exception.AccountNotFoundException;
import com.challenge.account.domain.exception.DuplicateAccountException;
import com.challenge.account.domain.model.Account;
import com.challenge.account.infrastructure.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {
        validateAccountNumber(request.accountNumber());

        Account account = new Account(
                request.accountNumber(),
                request.accountType(),
                request.initialBalance(),
                request.customerId()
        );

        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public List<AccountResponse> findAll() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    @Override
    public AccountResponse findById(Long id) {
        return accountRepository.findById(id)
                .map(accountMapper::toResponse)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Override
    public AccountResponse updateAccount(Long id, UpdateAccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        if (request.accountType() != null) {
            account.setAccountType(request.accountType());
        }
        if (request.status() != null) {
            account.setStatus(request.status());
        }

        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
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
}
