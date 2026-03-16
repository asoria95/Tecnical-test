package com.challenge.customer.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String identification;

    @Column(nullable = false)
    private String status = "ACTIVE";

    public Customer(String name, String identification, String status) {
        this.name = name;
        this.identification = identification;
        this.status = status != null ? status : "ACTIVE";
    }
}
