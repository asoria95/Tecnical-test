package com.challenge.account.api;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MovementRequest(
        @NotNull Long accountId,
        @NotNull BigDecimal amount
) {}
