package com.challenge.customer.api;

/**
 * Consistent error body for all REST error responses.
 * No stack traces or internal details are exposed.
 */
public record ErrorResponse(int status, String message) {
}
