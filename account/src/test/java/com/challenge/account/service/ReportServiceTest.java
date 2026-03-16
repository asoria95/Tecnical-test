package com.challenge.account.service;

import com.challenge.account.application.*;
import com.challenge.account.domain.model.Account;
import com.challenge.account.domain.model.Movement;
import com.challenge.account.infrastructure.repository.MovementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.challenge.account.domain.exception.CustomerNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private CustomerExistencePort customerExistencePort;

    @Mock
    private MovementRepository movementRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    void generateReport_returnsLinesWithCustomerNameFromPort() {
        Long clienteId = 1L;
        LocalDate inicio = LocalDate.of(2022, 2, 1);
        LocalDate fin = LocalDate.of(2022, 2, 10);
        when(customerExistencePort.getById(clienteId))
                .thenReturn(new CustomerDisplayData(1L, "Marianela Montalvo"));

        Account account = new Account("225487", "Corriente", new BigDecimal("100"), "1");
        account.setId(1L);
        account.setStatus(true);
        Movement movement = new Movement("Deposito", new BigDecimal("600"), new BigDecimal("700"), account);
        movement.setDate(LocalDateTime.of(2022, 2, 10, 12, 0));

        when(movementRepository.findByAccountCustomerIdAndDateBetween(
                eq("1"), eq(inicio.atStartOfDay()), eq(fin.plusDays(1).atStartOfDay())))
                .thenReturn(List.of(movement));

        List<ReportLineDto> result = reportService.generateReport(clienteId, inicio, fin);

        verify(customerExistencePort).getById(clienteId);
        assertThat(result).hasSize(1);
        ReportLineDto line = result.get(0);
        assertThat(line.cliente()).isEqualTo("Marianela Montalvo");
        assertThat(line.numeroCuenta()).isEqualTo("225487");
        assertThat(line.tipo()).isEqualTo("Corriente");
        assertThat(line.saldoInicial()).isEqualByComparingTo("100");
        assertThat(line.movimiento()).isEqualByComparingTo("600");
        assertThat(line.saldoDisponible()).isEqualByComparingTo("700");
        assertThat(line.estado()).isTrue();
    }

    @Test
    void generateReport_whenNoMovements_returnsEmptyList() {
        when(customerExistencePort.getById(1L)).thenReturn(new CustomerDisplayData(1L, "Jose Lema"));
        when(movementRepository.findByAccountCustomerIdAndDateBetween(
                eq("1"), eq(LocalDate.of(2022, 1, 1).atStartOfDay()), eq(LocalDate.of(2022, 1, 2).atStartOfDay())))
                .thenReturn(List.of());

        List<ReportLineDto> result = reportService.generateReport(1L, LocalDate.of(2022, 1, 1), LocalDate.of(2022, 1, 1));

        assertThat(result).isEmpty();
    }

    @Test
    void generateReport_whenCustomerNotFound_throwsCustomerNotFoundException() {
        when(customerExistencePort.getById(999L)).thenThrow(new CustomerNotFoundException(999L));

        assertThatThrownBy(() -> reportService.generateReport(999L, LocalDate.of(2022, 1, 1), LocalDate.of(2022, 1, 31)))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("999");

        verify(customerExistencePort).getById(999L);
    }
}
