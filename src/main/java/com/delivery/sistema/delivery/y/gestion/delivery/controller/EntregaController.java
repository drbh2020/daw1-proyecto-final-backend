package com.delivery.sistema.delivery.y.gestion.delivery.controller;

import com.delivery.sistema.delivery.y.gestion.delivery.dto.EntregaDto;
import com.delivery.sistema.delivery.y.gestion.delivery.service.EntregaService;
import com.delivery.sistema.delivery.y.gestion.delivery.model.EstadoEntrega;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/entregas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EntregaController {

    private final EntregaService entregaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EntregaDto>> listarEntregas(
            @RequestParam(required = false) EstadoEntrega estado,
            @RequestParam(required = false) Long repartidorId,
            Pageable pageable) {
        
        if (estado != null) {
            return ResponseEntity.ok(entregaService.listarEntregasPorEstado(estado, pageable));
        }
        if (repartidorId != null) {
            return ResponseEntity.ok(entregaService.listarEntregasPorRepartidor(repartidorId, pageable));
        }
        
        return ResponseEntity.ok(entregaService.listarEntregas(pageable));
    }

    @GetMapping("/activas")
    @PreAuthorize("hasRole('ADMIN') or hasRole('REPARTIDOR')")
    public ResponseEntity<Page<EntregaDto>> entregasActivas(Pageable pageable) {
        return ResponseEntity.ok(entregaService.listarEntregasActivas(pageable));
    }

    @GetMapping("/mis-entregas")
    @PreAuthorize("hasRole('REPARTIDOR')")
    public ResponseEntity<Page<EntregaDto>> misEntregas(
            @RequestParam(required = false) EstadoEntrega estado,
            Pageable pageable) {
        // El repartidorId se obtendría del authentication principal
        Long repartidorId = 1L; // TODO: obtener del auth
        
        if (estado != null) {
            return ResponseEntity.ok(entregaService.listarEntregasPorRepartidorYEstado(repartidorId, estado, pageable));
        }
        return ResponseEntity.ok(entregaService.listarEntregasPorRepartidor(repartidorId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @entregaService.esOwnerDeLaEntrega(#id, authentication.name)")
    public ResponseEntity<EntregaDto> obtenerEntrega(@PathVariable Long id) {
        return ResponseEntity.ok(entregaService.obtenerEntregaPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE')")
    public ResponseEntity<EntregaDto> crearEntrega(@Valid @RequestBody EntregaDto entregaDto) {
        return ResponseEntity.ok(entregaService.crearEntrega(entregaDto));
    }

    @PatchMapping("/{id}/asignar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE')")
    public ResponseEntity<EntregaDto> asignarRepartidor(
            @PathVariable Long id,
            @RequestParam Long repartidorId) {
        return ResponseEntity.ok(entregaService.asignarRepartidor(id, repartidorId));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN') or hasRole('REPARTIDOR')")
    public ResponseEntity<EntregaDto> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoEntrega estado) {
        return ResponseEntity.ok(entregaService.cambiarEstado(id, estado));
    }

    @PatchMapping("/{id}/iniciar")
    @PreAuthorize("hasRole('REPARTIDOR') and @entregaService.esOwnerDeLaEntrega(#id, authentication.name)")
    public ResponseEntity<EntregaDto> iniciarEntrega(@PathVariable Long id) {
        return ResponseEntity.ok(entregaService.iniciarEntrega(id));
    }

    @PatchMapping("/{id}/finalizar")
    @PreAuthorize("hasRole('REPARTIDOR') and @entregaService.esOwnerDeLaEntrega(#id, authentication.name)")
    public ResponseEntity<EntregaDto> finalizarEntrega(
            @PathVariable Long id,
            @RequestParam(required = false) String comentarios) {
        return ResponseEntity.ok(entregaService.finalizarEntrega(id, comentarios));
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        return ResponseEntity.ok(entregaService.obtenerEstadisticasEntregas());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntregaDto> actualizarEntrega(
            @PathVariable Long id,
            @Valid @RequestBody EntregaDto entregaDto) {
        return ResponseEntity.ok(entregaService.actualizarEntrega(id, entregaDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelarEntrega(@PathVariable Long id) {
        entregaService.cancelarEntrega(id);
        return ResponseEntity.noContent().build();
    }
}