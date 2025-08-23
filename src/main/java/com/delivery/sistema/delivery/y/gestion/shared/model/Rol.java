package com.delivery.sistema.delivery.y.gestion.shared.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rol")

public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // Ej: ROL_CLIENTE, ROL_RESTAURANTE, ROL_ADMIN
}
