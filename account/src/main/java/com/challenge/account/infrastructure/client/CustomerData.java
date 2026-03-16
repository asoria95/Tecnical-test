package com.challenge.account.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerData(
        Long id,
        String name,
        String status
) {}
