package com.challenge.account.application;

import com.challenge.account.domain.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse toResponse(Account account) {
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
