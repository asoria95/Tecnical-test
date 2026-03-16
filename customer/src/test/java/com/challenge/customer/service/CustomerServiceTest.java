package com.challenge.customer.service;

import com.challenge.customer.application.CreateCustomerCommand;
import com.challenge.customer.application.CustomerServiceImpl;
import com.challenge.customer.application.UpdateCustomerCommand;
import com.challenge.customer.domain.Customer;
import com.challenge.customer.domain.exception.CustomerNotFoundException;
import com.challenge.customer.infrastructure.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void shouldCreateCustomerWithMinimumDataAndReturnSavedCustomer() {
        CreateCustomerCommand command = new CreateCustomerCommand("John Doe", null, null, "12345678", null, null, "secret");
        Customer savedCustomer = new Customer("John Doe", null, null, "12345678", null, null, "secret", "ACTIVE");
        savedCustomer.setId(1L);

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = customerService.create(command);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getIdentification()).isEqualTo("12345678");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        Customer passedToRepository = captor.getValue();
        assertThat(passedToRepository.getName()).isEqualTo("John Doe");
        assertThat(passedToRepository.getIdentification()).isEqualTo("12345678");
        assertThat(passedToRepository.getPassword()).isEqualTo("secret");
        assertThat(passedToRepository.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void shouldReturnCustomerWhenFoundById() {
        Long customerId = 1L;
        Customer existingCustomer = new Customer("Jane Doe", null, null, "87654321", null, null, "pwd", "ACTIVE");
        existingCustomer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        Customer result = customerService.getById(customerId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(customerId);
        assertThat(result.getName()).isEqualTo("Jane Doe");
        assertThat(result.getIdentification()).isEqualTo("87654321");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        verify(customerRepository).findById(customerId);
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenCustomerDoesNotExist() {
        Long nonExistentId = 999L;
        when(customerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getById(nonExistentId))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("999");

        verify(customerRepository).findById(nonExistentId);
    }

    @Test
    void shouldUpdateExistingCustomerWithNewNameAndIdentification() {
        Long customerId = 1L;
        Customer existingCustomer = new Customer("Jane Doe", null, null, "87654321", null, null, "pwd", "ACTIVE");
        existingCustomer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateCustomerCommand command = new UpdateCustomerCommand(customerId, "Jane Updated", null, null, "11111111", null, null, null, null);
        Customer result = customerService.update(command);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(customerId);
        assertThat(result.getName()).isEqualTo("Jane Updated");
        assertThat(result.getIdentification()).isEqualTo("11111111");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        Customer saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Jane Updated");
        assertThat(saved.getIdentification()).isEqualTo("11111111");
        assertThat(saved.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void shouldPreserveStatusWhenUpdatingCustomerNameAndIdentification() {
        Long customerId = 2L;
        Customer existingCustomer = new Customer("John Smith", null, null, "55555555", null, null, "pwd", "INACTIVE");
        existingCustomer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateCustomerCommand command = new UpdateCustomerCommand(customerId, "John Updated", null, null, "66666666", null, null, null, null);
        Customer result = customerService.update(command);

        assertThat(result.getStatus()).isEqualTo("INACTIVE");

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("INACTIVE");
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenUpdatingNonExistentCustomer() {
        Long nonExistentId = 999L;
        when(customerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        UpdateCustomerCommand command = new UpdateCustomerCommand(nonExistentId, "Any Name", null, null, "00000000", null, null, null, null);

        assertThatThrownBy(() -> customerService.update(command))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("999");

        verify(customerRepository).findById(nonExistentId);
        verify(customerRepository, never()).save(any());
    }


    @Test
    void shouldDeleteExistingCustomerWhenFound() {
        Long customerId = 1L;
        Customer existingCustomer = new Customer("Jane Doe", null, null, "87654321", null, null, "pwd", "ACTIVE");
        existingCustomer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        customerService.delete(customerId);

        verify(customerRepository).findById(customerId);
        verify(customerRepository).deleteById(customerId);
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenDeletingNonExistentCustomer() {
        Long nonExistentId = 999L;
        when(customerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.delete(nonExistentId))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("999");

        verify(customerRepository).findById(nonExistentId);
        verify(customerRepository, never()).deleteById(any());
    }
}
