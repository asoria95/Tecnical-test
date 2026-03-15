package com.challenge.account.application;

import java.math.BigDecimal;

public record CreateAccountRequest(
        String accountNumber,
        String accountType,
        BigDecimal initialBalance,
        String customerId
) {}
