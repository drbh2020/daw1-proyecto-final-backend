package com.delivery.sistema.delivery.y.gestion.auth.controller;

import com.delivery.sistema.delivery.y.gestion.auth.dto.Registre;
import com.delivery.sistema.delivery.y.gestion.auth.dto.Solicitud;
import com.delivery.sistema.delivery.y.gestion.auth.dto.Respuesta;
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
    public ResponseEntity<Respuesta> registrar(@Valid @RequestBody Registre registro) {
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
    public ResponseEntity<Respuesta> login(@Valid @RequestBody Solicitud solicitud) {
        return ResponseEntity.ok(authServicio.login(solicitud));
    }

}
