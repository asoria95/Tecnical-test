package com.challenge.account.integration;

import com.challenge.account.api.MovementRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for GET /reportes (Estado de cuenta).
 * Uses mocked customer-service (BaseIntegrationTest) returning "Test Customer" for clienteId 1.
 */
class ReportApiIntegrationTest extends BaseIntegrationTest {

    @Test
    void getReport_returnsJsonWithPdfAlignedFields() throws Exception {
        Long accountId = createAccountAndGetId("225487", "100.00");
        registerMovement(accountId, "600.00");

        LocalDate today = LocalDate.now();
        String fecha = today.format(DateTimeFormatter.ISO_LOCAL_DATE);

        mockMvc.perform(get("/reportes")
                        .param("clienteId", "1")
                        .param("fechaInicio", fecha)
                        .param("fechaFin", fecha))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].Cliente").value("Test Customer"))
                .andExpect(jsonPath("$[0]['Numero Cuenta']").value("225487"))
                .andExpect(jsonPath("$[0].Tipo").value("Ahorros"))
                .andExpect(jsonPath("$[0].Movimiento").value(600.00))
                .andExpect(jsonPath("$[0]['Saldo Disponible']").value(700.00))
                .andExpect(jsonPath("$[0].Fecha").isNotEmpty())
                .andExpect(jsonPath("$[0]['Saldo Inicial']").value(100.00));
    }

    @Test
    void getReport_whenNoMovementsInRange_returnsEmptyArray() throws Exception {
        createAccountAndGetId("496825", "540.00");
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String fecha = tomorrow.format(DateTimeFormatter.ISO_LOCAL_DATE);

        mockMvc.perform(get("/reportes")
                        .param("clienteId", "1")
                        .param("fechaInicio", fecha)
                        .param("fechaFin", fecha))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private void registerMovement(Long accountId, String amount) throws Exception {
        MovementRequest request = new MovementRequest(accountId, new BigDecimal(amount));
        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
