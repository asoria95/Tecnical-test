package com.challenge.account.application;

import java.math.BigDecimal;
import java.util.List;

public interface MovementService {

    MovementResponse registerDeposit(RegisterMovementRequest request);

    MovementResponse registerWithdrawal(RegisterMovementRequest request);

    MovementResponse registerMovement(Long accountId, BigDecimal signedAmount);

    MovementResponse findById(Long id);

    List<MovementResponse> findByAccountId(Long accountId);
}
