package com.challenge.account.application;

import java.time.LocalDate;
import java.util.List;

/**
 * Generates "Estado de cuenta" report by customer and date range.
 * Uses customer-service (HTTP) for validation and customer display name.
 */
public interface ReportService {

    /**
     * Returns report lines for the given customer and date range.
     * Validates customer exists via customer-service; throws CustomerNotFoundException if not.
     */
    List<ReportLineDto> generateReport(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
