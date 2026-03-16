package com.challenge.customer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class Customer extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clienteid")
    private Long id;

    @Column(name = "contrasena", nullable = false)
    private String password;

    @Column(name = "estado", nullable = false)
    private String status = "ACTIVE";

    public Customer(String name, String gender, Integer age, String identification,
                    String address, String phone, String password, String status) {
        super(name, gender, age, identification, address, phone);
        this.password = password;
        this.status = status != null ? status : "ACTIVE";
    }
}
