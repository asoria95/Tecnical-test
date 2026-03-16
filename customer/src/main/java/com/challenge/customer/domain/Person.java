package com.challenge.customer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class Person {

    @Column(name = "nombre", nullable = false)
    private String name;

    @Column(name = "genero")
    private String gender;

    @Column(name = "edad")
    private Integer age;

    @Column(name = "identificacion", nullable = false, unique = true)
    private String identification;

    @Column(name = "direccion")
    private String address;

    @Column(name = "telefono")
    private String phone;

    protected Person(String name, String gender, Integer age, String identification, String address, String phone) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.identification = identification;
        this.address = address;
        this.phone = phone;
    }
}
