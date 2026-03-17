package com.challenge.customer.api;

import com.challenge.customer.application.CreateCustomerCommand;
import com.challenge.customer.application.CustomerMapper;
import com.challenge.customer.application.CustomerService;
import com.challenge.customer.application.UpdateCustomerCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    public CustomerController(CustomerService customerService, CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@RequestBody @Valid CreateCustomerRequest request) {
        CreateCustomerCommand command = new CreateCustomerCommand(
                request.name(),
                request.gender(),
                request.age(),
                request.identification(),
                request.address(),
                request.phone(),
                request.password()
        );
        var customer = customerService.create(command);
        var response = customerMapper.toResponse(customer);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(customer.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public List<CustomerResponse> getAll() {
        return customerService.getAll().stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @GetMapping("/buscar")
    public CustomerResponse findByName(@RequestParam String nombre) {
        return customerMapper.toResponse(customerService.findByName(nombre));
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) {
        return customerMapper.toResponse(customerService.getById(id));
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @RequestBody @Valid UpdateCustomerRequest request) {
        UpdateCustomerCommand command = new UpdateCustomerCommand(
                id,
                request.name(),
                request.gender(),
                request.age(),
                request.identification(),
                request.address(),
                request.phone(),
                request.password(),
                request.status()
        );
        return customerMapper.toResponse(customerService.update(command));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }
}
