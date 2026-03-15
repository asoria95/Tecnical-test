package com.challenge.account.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovementResponse(
        Long id,
        LocalDateTime date,
        String movementType,
        BigDecimal amount,
        BigDecimal balance
) {}
