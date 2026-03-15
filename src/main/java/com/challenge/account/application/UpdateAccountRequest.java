package com.challenge.account.application;

public record UpdateAccountRequest(
        String accountType,
        Boolean status
) {}
