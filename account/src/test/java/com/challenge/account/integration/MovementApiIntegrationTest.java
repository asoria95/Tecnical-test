package com.challenge.account.integration;

import com.challenge.account.api.MovementRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MovementApiIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldRegisterDepositAndIncreaseBalance() throws Exception {
        Long accountId = createAccountAndGetId("478758", "1000.00");

        MovementRequest deposit = new MovementRequest(accountId, new BigDecimal("500.00"));

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deposit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.movementType").value("Deposito"))
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.balance").value(1500.00))
                .andExpect(jsonPath("$.date").isNotEmpty());
    }

    @Test
    void shouldReflectDepositInAccountBalance() throws Exception {
        Long accountId = createAccountAndGetId("478758", "1000.00");

        registerMovement(accountId, "500.00");

        mockMvc.perform(get("/cuentas/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500.00));
    }

    @Test
    void shouldRegisterWithdrawalAndDecreaseBalance() throws Exception {
        Long accountId = createAccountAndGetId("478758", "1000.00");

        MovementRequest withdrawal = new MovementRequest(accountId, new BigDecimal("-200.00"));

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawal)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.movementType").value("Retiro"))
                .andExpect(jsonPath("$.amount").value(-200.00))
                .andExpect(jsonPath("$.balance").value(800.00));
    }

    @Test
    void shouldRejectWithdrawalWithInsufficientBalance() throws Exception {
        Long accountId = createAccountAndGetId("478758", "1000.00");

        MovementRequest withdrawal = new MovementRequest(accountId, new BigDecimal("-1500.00"));

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawal)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Saldo no disponible"));
    }

    @Test
    void shouldPreserveBalanceAfterRejectedWithdrawal() throws Exception {
        Long accountId = createAccountAndGetId("478758", "1000.00");

        MovementRequest withdrawal = new MovementRequest(accountId, new BigDecimal("-1500.00"));
        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawal)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/cuentas/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void shouldRejectZeroAmountMovement() throws Exception {
        Long accountId = createAccountAndGetId("478758", "1000.00");

        MovementRequest zeroMovement = new MovementRequest(accountId, BigDecimal.ZERO);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zeroMovement)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private void registerMovement(Long accountId, String amount) throws Exception {
        MovementRequest request = new MovementRequest(accountId, new BigDecimal(amount));

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
