package com.delivery.sistema.delivery.y.gestion.auth.dto;

import lombok.Getter;
import lombok.Setter;

// DTO para solicitudes de login con credenciales de usuario
@Getter
@Setter
public class Solicitud {
    private String nombre;
    private String email;
    private String password;
    private String direccion;
}
