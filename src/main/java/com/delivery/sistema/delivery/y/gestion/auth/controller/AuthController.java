package com.delivery.sistema.delivery.y.gestion.auth.controller;

import com.delivery.sistema.delivery.y.gestion.auth.dto.RegistroClienteDto;
import com.delivery.sistema.delivery.y.gestion.auth.dto.LoginRequestDto;
import com.delivery.sistema.delivery.y.gestion.auth.dto.LoginResponseDto;
import com.delivery.sistema.delivery.y.gestion.auth.service.AuthServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro y autenticación de usuarios")
public class AuthController {

    private final AuthServicio authServicio;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", 
               description = "Permite registrar un nuevo cliente en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "El usuario ya existe")
    })
    public ResponseEntity<LoginResponseDto> registrar(@Valid @RequestBody RegistroClienteDto registro) {
        return ResponseEntity.ok(authServicio.registrar(registro));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", 
               description = "Permite autenticar un usuario y obtener token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso, token JWT generado"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authServicio.login(loginRequestDto));
    }

}
