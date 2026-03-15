package com.challenge.account.integration;

import com.challenge.account.application.CreateAccountRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountApiIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldCreateAccountAndReturnCreatedStatus() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest(
                "478758", "Ahorros", new BigDecimal("1000.00"), "customer-1"
        );

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.accountNumber").value("478758"))
                .andExpect(jsonPath("$.accountType").value("Ahorros"))
                .andExpect(jsonPath("$.initialBalance").value(1000.00))
                .andExpect(jsonPath("$.balance").value(1000.00))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.customerId").value("customer-1"));
    }

    @Test
    void shouldRetrieveCreatedAccountById() throws Exception {
        Long accountId = createAccountAndGetId("478758", "1000.00");

        mockMvc.perform(get("/cuentas/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId))
                .andExpect(jsonPath("$.accountNumber").value("478758"))
                .andExpect(jsonPath("$.accountType").value("Ahorros"))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void shouldRejectDuplicateAccountNumber() throws Exception {
        createAccountAndGetId("478758", "1000.00");

        CreateAccountRequest duplicate = new CreateAccountRequest(
                "478758", "Corriente", new BigDecimal("500.00"), "customer-2"
        );

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void shouldRejectCreateRequestWithMissingFields() throws Exception {
        String invalidBody = """
                { "accountType": "Ahorros" }
                """;

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
