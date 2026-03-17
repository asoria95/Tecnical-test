package com.challenge.account.infrastructure.client;

import com.challenge.account.domain.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CustomerServiceClient {

    private final RestClient restClient;

    public CustomerServiceClient(
            @Value("${customer.service.base-url:http://localhost:8081}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public CustomerData getById(Long customerId) {
        return restClient.get()
                .uri("/clientes/{id}", customerId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    if (response.getStatusCode().value() == 404) {
                        throw new CustomerNotFoundException(customerId);
                    }
                    throw new IllegalStateException(
                            "Customer service returned " + response.getStatusCode());
                })
                .body(CustomerData.class);
    }

    public CustomerData getByName(String name) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/clientes/buscar").queryParam("nombre", name).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    if (response.getStatusCode().value() == 404) {
                        throw new CustomerNotFoundException("Customer not found with name: " + name);
                    }
                    throw new IllegalStateException(
                            "Customer service returned " + response.getStatusCode());
                })
                .body(CustomerData.class);
    }
}
