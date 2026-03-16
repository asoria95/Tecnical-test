package com.challenge.account.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO for customer-service GET /clientes/{id} response.
 * Keeps account-service independent from customer-service's API types.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerData(
        Long id,
        String name,
        String status
) {}
