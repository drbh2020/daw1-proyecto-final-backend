package com.delivery.sistema.delivery.y.gestion.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// DTO para respuesta de autenticación con token JWT
@Setter
@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
}
