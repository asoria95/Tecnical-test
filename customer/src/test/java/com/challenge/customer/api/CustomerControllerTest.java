package com.challenge.customer.api;

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
class CustomerControllerTest {

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void create_returns201AndCustomerResponse() throws Exception {
        var request = new CreateCustomerRequest("John Doe", null, null, "ID-123", null, null, "pass123");

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
    void create_withBlankName_returns400() throws Exception {
        var request = new CreateCustomerRequest("", null, null, "ID-123", null, null, "pass");

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_withBlankPassword_returns400() throws Exception {
        var request = new CreateCustomerRequest("John", null, null, "ID-123", null, null, "   ");

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_returns200AndCustomerResponse() throws Exception {
        var createRequest = new CreateCustomerRequest("Jane", null, null, "ID-456", null, null, "pass");
        String location = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        Long id = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));

        mockMvc.perform(get("/clientes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Jane"))
                .andExpect(jsonPath("$.identification").value("ID-456"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getById_whenNotFound_returns404() throws Exception {
        mockMvc.perform(get("/clientes/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("99999")));
    }

    @Test
    void update_returns200AndCustomerResponse() throws Exception {
        var createRequest = new CreateCustomerRequest("Jane", null, null, "ID-456", null, null, "pass");
        String location = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        Long id = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));

        var updateRequest = new UpdateCustomerRequest("Jane Updated", null, null, "ID-456-NEW", null, null, null, null);

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
    void delete_returns204() throws Exception {
        var createRequest = new CreateCustomerRequest("To Delete", null, null, "ID-DEL", null, null, "pass");
        String location = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        Long id = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));

        mockMvc.perform(delete("/clientes/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/clientes/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_whenNotFound_returns404() throws Exception {
        mockMvc.perform(delete("/clientes/99999"))
                .andExpect(status().isNotFound());
    }
}
