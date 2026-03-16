package com.challenge.account.application;

import com.challenge.account.domain.exception.CustomerNotFoundException;

public interface CustomerExistencePort {

    void validateExists(Long customerId) throws CustomerNotFoundException;

    CustomerDisplayData getById(Long customerId) throws CustomerNotFoundException;
}
