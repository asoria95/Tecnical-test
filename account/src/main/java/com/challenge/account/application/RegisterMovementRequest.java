package com.challenge.account.application;

import java.math.BigDecimal;

public record RegisterMovementRequest(
        Long accountId,
        BigDecimal amount
) {}
