package com.challenge.account.domain.exception;

public class DuplicateAccountException extends BusinessException {

    public DuplicateAccountException(String accountNumber) {
        super("Account number already exists: " + accountNumber);
    }
}
