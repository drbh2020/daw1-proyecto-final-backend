package com.delivery.sistema.delivery.y.gestion.cliente.controller;

import com.delivery.sistema.delivery.y.gestion.cliente.dto.ClienteDto;
import com.delivery.sistema.delivery.y.gestion.cliente.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ClienteDto>> listarClientes(Pageable pageable) {
        return ResponseEntity.ok(clienteService.listarClientes(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @clienteService.obtenerEmailPorId(#id)")
    public ResponseEntity<ClienteDto> obtenerCliente(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerClientePorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @clienteService.obtenerEmailPorId(#id)")
    public ResponseEntity<ClienteDto> actualizarCliente(
            @PathVariable Long id, 
            @Valid @RequestBody ClienteDto clienteDto) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, clienteDto));
    }

    @GetMapping("/{id}/estadisticas")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @clienteService.obtenerEmailPorId(#id)")
    public ResponseEntity<?> obtenerEstadisticasCliente(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerEstadisticasBasicas());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}