package com.delivery.sistema.delivery.y.gestion.auth.controller;

import com.delivery.sistema.delivery.y.gestion.auth.dto.Registre;
import com.delivery.sistema.delivery.y.gestion.auth.dto.Solicitud;
import com.delivery.sistema.delivery.y.gestion.auth.dto.Respuesta;
import com.delivery.sistema.delivery.y.gestion.auth.service.AuthServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthServicio authServicio;

    @PostMapping("/register")
    public ResponseEntity<Respuesta> registrar(@RequestBody Registre registro) {
        return ResponseEntity.ok(authServicio.registrar(registro));
    }

    @PostMapping("/login")
    public ResponseEntity<Respuesta> login(@RequestBody Solicitud solicitud) {
        return ResponseEntity.ok(authServicio.login(solicitud));
    }

}
