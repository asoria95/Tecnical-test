package com.challenge.account.application;

import com.challenge.account.domain.exception.CustomerNotFoundException;

/**
 * Outbound port: customer validation and display data from customer-service (synchronous HTTP).
 * Used by account creation (validateExists) and by reports (getById for customer name).
 */
public interface CustomerExistencePort {

    /**
     * Validates that the customer exists. Throws CustomerNotFoundException if not.
     */
    void validateExists(Long customerId) throws CustomerNotFoundException;

    /**
     * Fetches customer display data. Throws CustomerNotFoundException if customer-service returns 404.
     * Used by report generation to include customer name in the response.
     */
    CustomerDisplayData getById(Long customerId) throws CustomerNotFoundException;
}
