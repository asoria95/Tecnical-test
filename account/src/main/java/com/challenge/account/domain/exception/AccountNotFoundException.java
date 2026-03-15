package com.challenge.account.domain.exception;

public class AccountNotFoundException extends BusinessException {

    public AccountNotFoundException(Long accountId) {
        super("Account not found with id: " + accountId);
    }
}
