package com.delivery.sistema.delivery.y.gestion.auth.dto;

import lombok.Getter;
import lombok.Setter;

// DTO para registro de nuevos clientes en el sistema
@Getter
@Setter
public class Registre {
    private String nombre;
    private String email;
    private String password;
    private String direccion;
}
