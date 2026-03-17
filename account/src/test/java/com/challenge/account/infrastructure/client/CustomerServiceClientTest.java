package com.challenge.account.infrastructure.client;

import com.challenge.account.domain.exception.CustomerNotFoundException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerServiceClientTest {

    private MockWebServer mockWebServer;
    private CustomerServiceClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        client = new CustomerServiceClient(mockWebServer.url("/").toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getById_returnsCustomerDataWhenCustomerServiceReturns200() throws Exception {
        String json = """
                {"id": 1, "name": "Marianela Montalvo", "status": "ACTIVE"}
                """;
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        CustomerData result = client.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Marianela Montalvo");
        assertThat(result.status()).isEqualTo("ACTIVE");
    }

    @Test
    void getById_throwsCustomerNotFoundExceptionWhenCustomerServiceReturns404() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThatThrownBy(() -> client.getById(999L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getByName_returnsCustomerDataWhenCustomerServiceReturns200() throws Exception {
        String json = """
                {"id": 1, "name": "Jose Lema", "status": "ACTIVE"}
                """;
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        CustomerData result = client.getByName("Jose Lema");

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Jose Lema");
        assertThat(result.status()).isEqualTo("ACTIVE");
    }

    @Test
    void getByName_throwsCustomerNotFoundExceptionWhenCustomerServiceReturns404() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThatThrownBy(() -> client.getByName("Unknown Name"))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Unknown Name");
    }
}
