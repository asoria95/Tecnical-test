package com.challenge.account.domain.exception;

public class MovementNotFoundException extends BusinessException {

    public MovementNotFoundException(Long id) {
        super("Movement not found with id: " + id);
    }
}
