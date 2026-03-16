package com.challenge.account.infrastructure.client;

import com.challenge.account.application.CustomerDisplayData;
import com.challenge.account.application.CustomerExistencePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Infrastructure adapter: validates customer existence and fetches customer data via customer-service HTTP API.
 * 404 from customer-service is translated to CustomerNotFoundException by the client.
 */
@Component
public class CustomerExistenceAdapter implements CustomerExistencePort {

    private final CustomerServiceClient customerServiceClient;

    @Autowired
    public CustomerExistenceAdapter(CustomerServiceClient customerServiceClient) {
        this.customerServiceClient = customerServiceClient;
    }

    @Override
    public void validateExists(Long customerId) {
        customerServiceClient.getById(customerId);
    }

    @Override
    public CustomerDisplayData getById(Long customerId) {
        CustomerData data = customerServiceClient.getById(customerId);
        return new CustomerDisplayData(data.id(), data.name());
    }
}
