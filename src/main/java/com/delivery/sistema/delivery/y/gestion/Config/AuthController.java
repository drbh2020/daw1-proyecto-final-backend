package com.delivery.sistema.delivery.y.gestion.Config;

import com.delivery.sistema.delivery.y.gestion.Dto.Solicitud;
import com.delivery.sistema.delivery.y.gestion.Dto.Respuesta;
import com.delivery.sistema.delivery.y.gestion.Services.AuthServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthServicio authServicio;

    @PostMapping("/register")
    public ResponseEntity<Respuesta> registrar(@RequestBody Solicitud solicitud) {
        return ResponseEntity.ok(authServicio.registrar(solicitud));
    }

    @PostMapping("/login")
    public ResponseEntity<Respuesta> login(@RequestBody Solicitud solicitud) {
        return ResponseEntity.ok(authServicio.login(solicitud));
    }

}
