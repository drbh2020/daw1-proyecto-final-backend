package com.delivery.sistema.delivery.y.gestion.auth.controller;

import com.delivery.sistema.delivery.y.gestion.auth.dto.RegistroClienteDto;
import com.delivery.sistema.delivery.y.gestion.auth.dto.LoginResponseDto;
import com.delivery.sistema.delivery.y.gestion.auth.dto.LoginRequestDto;
import com.delivery.sistema.delivery.y.gestion.auth.service.AuthServicio;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/autenticacion")
public class ControllerAutenticacion {
    private final AuthServicio authServicio;

    public ControllerAutenticacion(AuthServicio authServicio) {
        this.authServicio = authServicio;
    }

    @PostMapping("/registro")
    public ResponseEntity<LoginResponseDto> registrar(@Valid @RequestBody RegistroClienteDto registro) {
        return ResponseEntity.ok(authServicio.registrar(registro));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authServicio.login(loginRequestDto));
    }
}
