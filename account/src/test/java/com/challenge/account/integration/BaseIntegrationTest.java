package com.challenge.account.integration;

import com.challenge.account.application.CreateAccountRequest;
import com.challenge.account.application.CustomerDisplayData;
import com.challenge.account.application.CustomerExistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@Import(BaseIntegrationTest.MockCustomerExistenceConfig.class)
abstract class BaseIntegrationTest {

    @Configuration
    static class MockCustomerExistenceConfig {
        @Bean
        @Primary
        CustomerExistencePort customerExistencePort() {
            CustomerExistencePort port = mock(CustomerExistencePort.class);
            doNothing().when(port).validateExists(anyLong());
            when(port.getById(anyLong())).thenReturn(new CustomerDisplayData(1L, "Test Customer"));
            when(port.findByName(anyString())).thenReturn(new CustomerDisplayData(1L, "Test Customer"));
            return port;
        }
    }

    @Autowired
    private WebApplicationContext context;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    protected Long createAccountAndGetId(String accountNumber, String initialBalance) throws Exception {
        CreateAccountRequest request = new CreateAccountRequest(
                accountNumber, "Ahorros", new BigDecimal(initialBalance), "Test Customer"
        );

        String response = mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }
}
