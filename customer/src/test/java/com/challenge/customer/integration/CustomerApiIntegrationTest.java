package com.challenge.customer.integration;

import com.challenge.customer.api.CreateCustomerRequest;
import com.challenge.customer.api.UpdateCustomerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class CustomerApiIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void shouldCreateCustomerWhenValidRequest() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest("Integration User", null, null, "ID-INT-001", null, null, "secret123");

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/clientes/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Integration User"))
                .andExpect(jsonPath("$.identification").value("ID-INT-001"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldGetCustomerWhenCustomerExists() throws Exception {
        Long id = createCustomerAndGetId("Jane Doe", "ID-GET-001");

        mockMvc.perform(get("/clientes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.identification").value("ID-GET-001"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldUpdateCustomerWhenCustomerExists() throws Exception {
        Long id = createCustomerAndGetId("Original Name", "ID-UPD-001");
        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest("Updated Name", null, null, "ID-UPD-002", null, null, null, null);

        mockMvc.perform(put("/clientes/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.identification").value("ID-UPD-002"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(get("/clientes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.identification").value("ID-UPD-002"));
    }

    @Test
    void shouldDeleteCustomerWhenCustomerExists() throws Exception {
        Long id = createCustomerAndGetId("To Delete", "ID-DEL-001");

        mockMvc.perform(delete("/clientes/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/clientes/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString(String.valueOf(id))));
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistentCustomer() throws Exception {
        long nonExistentId = 99_999L;

        mockMvc.perform(get("/clientes/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("99999")));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentCustomer() throws Exception {
        long nonExistentId = 99_998L;
        UpdateCustomerRequest request = new UpdateCustomerRequest("Any", null, null, "ID-ANY", null, null, null, null);

        mockMvc.perform(put("/clientes/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("99998")));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentCustomer() throws Exception {
        long nonExistentId = 99_997L;

        mockMvc.perform(delete("/clientes/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("99997")));
    }

    private Long createCustomerAndGetId(String name, String identification) throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(name, null, null, identification, null, null, "password");
        String location = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }
}
