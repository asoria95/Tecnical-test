package com.challenge.account.application;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    List<ReportLineDto> generateReport(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
