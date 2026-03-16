package com.challenge.account.application;

import com.challenge.account.domain.model.Movement;
import com.challenge.account.infrastructure.repository.MovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private static final DateTimeFormatter REPORT_DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy");

    private final CustomerExistencePort customerExistencePort;
    private final MovementRepository movementRepository;

    @Autowired
    public ReportServiceImpl(CustomerExistencePort customerExistencePort,
                             MovementRepository movementRepository) {
        this.customerExistencePort = customerExistencePort;
        this.movementRepository = movementRepository;
    }

    @Override
    public List<ReportLineDto> generateReport(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        CustomerDisplayData customer = customerExistencePort.getById(clienteId);
        String customerIdStr = String.valueOf(clienteId);
        LocalDateTime from = fechaInicio.atStartOfDay();
        LocalDateTime to = fechaFin.plusDays(1).atStartOfDay();

        return movementRepository.findByAccountCustomerIdAndDateBetween(customerIdStr, from, to).stream()
                .sorted(Comparator.comparing(Movement::getDate))
                .map(m -> toReportLine(m, customer.name()))
                .toList();
    }

    private ReportLineDto toReportLine(Movement movement, String customerName) {
        BigDecimal balanceAfter = movement.getBalance();
        BigDecimal amount = movement.getAmount();
        BigDecimal balanceBefore = balanceAfter.subtract(amount);

        return new ReportLineDto(
                movement.getDate().format(REPORT_DATE_FORMAT),
                customerName,
                movement.getAccount().getAccountNumber(),
                movement.getAccount().getAccountType(),
                balanceBefore,
                movement.getAccount().getStatus(),
                amount,
                balanceAfter
        );
    }
}
