package com.delivery.sistema.delivery.y.gestion.delivery.controller;

import com.delivery.sistema.delivery.y.gestion.delivery.dto.RepartidorDto;
import com.delivery.sistema.delivery.y.gestion.delivery.service.RepartidorService;
import com.delivery.sistema.delivery.y.gestion.delivery.model.EstadoRepartidor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/repartidores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RepartidorController {

    private final RepartidorService repartidorService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<RepartidorDto>> listarRepartidores(
            @RequestParam(required = false) Boolean disponible,
            @RequestParam(required = false) EstadoRepartidor estado,
            Pageable pageable) {
        
        if (disponible != null && disponible) {
            return ResponseEntity.ok(repartidorService.listarRepartidoresDisponibles(pageable));
        }
        if (estado != null) {
            return ResponseEntity.ok(repartidorService.listarRepartidoresPorEstado(estado, pageable));
        }
        
        return ResponseEntity.ok(repartidorService.listarRepartidores(pageable));
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE')")
    public ResponseEntity<Page<RepartidorDto>> repartidoresDisponibles(Pageable pageable) {
        return ResponseEntity.ok(repartidorService.listarRepartidoresDisponibles(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('REPARTIDOR') and @repartidorService.esOwner(#id, authentication.name))")
    public ResponseEntity<RepartidorDto> obtenerRepartidor(@PathVariable Long id) {
        return ResponseEntity.ok(repartidorService.obtenerRepartidorPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepartidorDto> crearRepartidor(@Valid @RequestBody RepartidorDto repartidorDto) {
        return ResponseEntity.ok(repartidorService.crearRepartidor(repartidorDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('REPARTIDOR') and @repartidorService.esOwner(#id, authentication.name))")
    public ResponseEntity<RepartidorDto> actualizarRepartidor(
            @PathVariable Long id,
            @Valid @RequestBody RepartidorDto repartidorDto) {
        return ResponseEntity.ok(repartidorService.actualizarRepartidor(id, repartidorDto));
    }

    @PatchMapping("/{id}/disponibilidad")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('REPARTIDOR') and @repartidorService.esOwner(#id, authentication.name))")
    public ResponseEntity<RepartidorDto> cambiarDisponibilidad(
            @PathVariable Long id,
            @RequestParam boolean disponible) {
        return ResponseEntity.ok(repartidorService.cambiarDisponibilidad(id, disponible));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('REPARTIDOR') and @repartidorService.esOwner(#id, authentication.name))")
    public ResponseEntity<RepartidorDto> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoRepartidor estado) {
        return ResponseEntity.ok(repartidorService.cambiarEstado(id, estado));
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        return ResponseEntity.ok(repartidorService.obtenerEstadisticasRepartidores());
    }

    @GetMapping("/{id}/historial-entregas")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('REPARTIDOR') and @repartidorService.esOwner(#id, authentication.name))")
    public ResponseEntity<?> historialEntregas(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(repartidorService.obtenerHistorialEntregas(id, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarRepartidor(@PathVariable Long id) {
        repartidorService.eliminarRepartidor(id);
        return ResponseEntity.noContent().build();
    }
}