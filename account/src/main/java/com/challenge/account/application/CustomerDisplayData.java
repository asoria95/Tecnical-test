package com.challenge.account.application;

/**
 * Application-level DTO for customer display data (e.g. for reports).
 * Sourced from customer-service via synchronous HTTP; keeps application independent of client DTOs.
 */
public record CustomerDisplayData(Long id, String name) {}
