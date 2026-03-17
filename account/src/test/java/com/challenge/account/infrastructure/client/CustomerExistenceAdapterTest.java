package com.challenge.account.infrastructure.client;

import com.challenge.account.domain.exception.CustomerNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerExistenceAdapterTest {

    @Mock
    private CustomerServiceClient customerServiceClient;

    @InjectMocks
    private CustomerExistenceAdapter customerExistenceAdapter;

    @Test
    void shouldPassWhenCustomerExists() {
        Long customerId = 1L;
        when(customerServiceClient.getById(customerId))
                .thenReturn(new CustomerData(1L, "John", "ACTIVE"));

        customerExistenceAdapter.validateExists(customerId);

        verify(customerServiceClient).getById(customerId);
    }

    @Test
    void shouldThrowWhenCustomerNotFound() {
        Long customerId = 999L;
        when(customerServiceClient.getById(customerId))
                .thenThrow(new CustomerNotFoundException(customerId));

        assertThatThrownBy(() -> customerExistenceAdapter.validateExists(customerId))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("999");

        verify(customerServiceClient).getById(customerId);
    }

    @Test
    void getById_returnsCustomerDisplayDataMappedFromClient() {
        Long customerId = 2L;
        when(customerServiceClient.getById(customerId))
                .thenReturn(new CustomerData(2L, "Marianela Montalvo", "ACTIVE"));

        var result = customerExistenceAdapter.getById(customerId);

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo("Marianela Montalvo");
        verify(customerServiceClient).getById(customerId);
    }

    @Test
    void findByName_returnsCustomerDisplayDataMappedFromClient() {
        when(customerServiceClient.getByName("Marianela Montalvo"))
                .thenReturn(new CustomerData(2L, "Marianela Montalvo", "ACTIVE"));

        var result = customerExistenceAdapter.findByName("Marianela Montalvo");

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo("Marianela Montalvo");
        verify(customerServiceClient).getByName("Marianela Montalvo");
    }

    @Test
    void findByName_throwsWhenCustomerServiceReturnsNotFound() {
        when(customerServiceClient.getByName("Unknown"))
                .thenThrow(new CustomerNotFoundException("Customer not found with name: Unknown"));

        assertThatThrownBy(() -> customerExistenceAdapter.findByName("Unknown"))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Unknown");
    }
}
