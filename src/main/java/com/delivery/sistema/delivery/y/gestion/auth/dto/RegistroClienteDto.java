package com.delivery.sistema.delivery.y.gestion.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

// DTO para registro de nuevos clientes en el sistema
@Getter
@Setter
public class RegistroClienteDto {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe proporcionar un email válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 255, message = "La contraseña debe tener entre 6 y 255 caracteres")
    private String password;
    
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;
}
