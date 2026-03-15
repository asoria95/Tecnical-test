package com.challenge.account.application;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNumber,
        String accountType,
        BigDecimal initialBalance,
        BigDecimal balance,
        Boolean status,
        String customerId
) {}
