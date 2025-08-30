package com.delivery.sistema.delivery.y.gestion.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

// DTO para solicitudes de login con credenciales de usuario
@Getter
@Setter
public class LoginRequestDto {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe proporcionar un email válido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
