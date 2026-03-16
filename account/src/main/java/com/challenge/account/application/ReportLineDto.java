package com.challenge.account.application;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * One line of the "Estado de cuenta" report. Field names aligned with PDF example JSON.
 */
public record ReportLineDto(
        @JsonProperty("Fecha") String fecha,
        @JsonProperty("Cliente") String cliente,
        @JsonProperty("Numero Cuenta") String numeroCuenta,
        @JsonProperty("Tipo") String tipo,
        @JsonProperty("Saldo Inicial") BigDecimal saldoInicial,
        @JsonProperty("Estado") Boolean estado,
        @JsonProperty("Movimiento") BigDecimal movimiento,
        @JsonProperty("Saldo Disponible") BigDecimal saldoDisponible
) {}
