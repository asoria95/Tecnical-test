package com.challenge.account.application;

import com.challenge.account.domain.model.Movement;
import org.springframework.stereotype.Component;

@Component
public class MovementMapper {

    public MovementResponse toResponse(Movement movement) {
        return new MovementResponse(
                movement.getId(),
                movement.getDate(),
                movement.getMovementType(),
                movement.getAmount(),
                movement.getBalance()
        );
    }
}
