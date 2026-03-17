package com.challenge.account.application;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank String accountNumber,
        @NotBlank String accountType,
        @NotNull @DecimalMin("0.00") BigDecimal initialBalance,
        @NotBlank String customerName
) {}
