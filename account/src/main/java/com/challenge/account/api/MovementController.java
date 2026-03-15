package com.challenge.account.api;

import com.challenge.account.application.MovementResponse;
import com.challenge.account.application.MovementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
public class MovementController {

    private final MovementService movementService;

    @Autowired
    public MovementController(MovementService movementService) {
        this.movementService = movementService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovementResponse register(@Valid @RequestBody MovementRequest request) {
        return movementService.registerMovement(request.accountId(), request.amount());
    }

    @GetMapping("/{id}")
    public MovementResponse findById(@PathVariable Long id) {
        return movementService.findById(id);
    }

    @GetMapping
    public List<MovementResponse> findByAccount(@RequestParam Long accountId) {
        return movementService.findByAccountId(accountId);
    }
}
