package com.challenge.account.infrastructure.client;

import com.challenge.account.application.CustomerDisplayData;
import com.challenge.account.application.CustomerExistencePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Override
    public CustomerDisplayData findByName(String name) {
        CustomerData data = customerServiceClient.getByName(name);
        return new CustomerDisplayData(data.id(), data.name());
    }
}
