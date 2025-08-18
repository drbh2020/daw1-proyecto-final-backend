package com.delivery.sistema.delivery.y.gestion.Controller;

import com.delivery.sistema.delivery.y.gestion.Dto.Registre;
import com.delivery.sistema.delivery.y.gestion.Dto.Respuesta;
import com.delivery.sistema.delivery.y.gestion.Dto.Solicitud;
import com.delivery.sistema.delivery.y.gestion.Services.Servicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/autenticacion")
public class ControllerAutenticacion {
    private final Servicio servicio;

    public ControllerAutenticacion(Servicio servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/registro")
    public ResponseEntity<Respuesta> registrar(@RequestBody Registre registro) {
        return ResponseEntity.ok(servicio.registrar(registro));
    }

    @PostMapping("/login")
    public ResponseEntity<Respuesta> login(@RequestBody Solicitud solicitud) {
        return ResponseEntity.ok(servicio.login(solicitud));
    }
}
