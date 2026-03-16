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
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class CustomerApiIntegrationTest {

    private static final long NON_EXISTENT_ID = 99_999L;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void shouldCreateCustomerWhenValidRequest() throws Exception {
        var request = new CreateCustomerRequest("John Doe", "ID-123");

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/clientes/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.identification").value("ID-123"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldReturnCustomerWhenFoundById() throws Exception {
        var createRequest = new CreateCustomerRequest("Jane", "ID-456");
        String location = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        long id = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));

        mockMvc.perform(get("/clientes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Jane"))
                .andExpect(jsonPath("$.identification").value("ID-456"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldUpdateCustomerWhenExistingId() throws Exception {
        var createRequest = new CreateCustomerRequest("Jane", "ID-456");
        String location = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        long id = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));

        var updateRequest = new UpdateCustomerRequest("Jane Updated", "ID-456-NEW");

        mockMvc.perform(put("/clientes/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Jane Updated"))
                .andExpect(jsonPath("$.identification").value("ID-456-NEW"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldDeleteCustomerWhenExistingId() throws Exception {
        var createRequest = new CreateCustomerRequest("To Delete", "ID-DEL");
        String location = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        long id = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));

        mockMvc.perform(delete("/clientes/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/clientes/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenGettingNonExistentCustomer() throws Exception {
        mockMvc.perform(get("/clientes/" + NON_EXISTENT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString(String.valueOf(NON_EXISTENT_ID))));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentCustomer() throws Exception {
        var updateRequest = new UpdateCustomerRequest("Any Name", "ANY-ID");

        mockMvc.perform(put("/clientes/" + NON_EXISTENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString(String.valueOf(NON_EXISTENT_ID))));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentCustomer() throws Exception {
        mockMvc.perform(delete("/clientes/" + NON_EXISTENT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString(String.valueOf(NON_EXISTENT_ID))));
    }

    @Test
    void shouldReturn400WhenCreatingCustomerWithBlankName() throws Exception {
        var request = new CreateCustomerRequest("", "ID-123");

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
