package com.delivery.sistema.delivery.y.gestion.pedido.controller;

import com.delivery.sistema.delivery.y.gestion.pedido.dto.PedidoDto;
import com.delivery.sistema.delivery.y.gestion.pedido.dto.CrearPedidoDto;
import com.delivery.sistema.delivery.y.gestion.pedido.service.PedidoService;
import com.delivery.sistema.delivery.y.gestion.pedido.model.EstadoPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PedidoDto>> listarPedidos(
            @RequestParam(required = false) EstadoPedido estado,
            @RequestParam(required = false) Long restauranteId,
            @RequestParam(required = false) Long clienteId,
            Pageable pageable) {
        
        if (estado != null) {
            return ResponseEntity.ok(pedidoService.listarPedidosPorEstado(estado, pageable));
        }
        if (restauranteId != null) {
            return ResponseEntity.ok(pedidoService.listarPedidosPorRestaurante(restauranteId, pageable));
        }
        if (clienteId != null) {
            return ResponseEntity.ok(pedidoService.listarPedidosPorCliente(clienteId, pageable));
        }
        
        return ResponseEntity.ok(pedidoService.listarPedidos(pageable));
    }

    @GetMapping("/mis-pedidos")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Page<PedidoDto>> misPedidos(
            @RequestParam(required = false) EstadoPedido estado,
            Pageable pageable) {
        // El clienteId se obtendría del authentication principal
        return ResponseEntity.ok(pedidoService.listarPedidosPorCliente(1L, pageable)); // TODO: obtener del auth
    }

    @GetMapping("/restaurante/{restauranteId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteService.esOwner(#restauranteId, authentication.name))")
    public ResponseEntity<Page<PedidoDto>> pedidosDelRestaurante(
            @PathVariable Long restauranteId,
            @RequestParam(required = false) EstadoPedido estado,
            Pageable pageable) {
        if (estado != null) {
            return ResponseEntity.ok(pedidoService.listarPedidosPorRestauranteYEstado(restauranteId, estado, pageable));
        }
        return ResponseEntity.ok(pedidoService.listarPedidosPorRestaurante(restauranteId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @pedidoService.esOwnerDelPedido(#id, authentication.name)")
    public ResponseEntity<PedidoDto> obtenerPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PedidoDto> crearPedido(@Valid @RequestBody CrearPedidoDto crearPedidoDto) {
        return ResponseEntity.ok(pedidoService.crearPedido(crearPedidoDto));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE') or hasRole('REPARTIDOR')")
    public ResponseEntity<PedidoDto> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, estado));
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        return ResponseEntity.ok(pedidoService.contarPedidosPorEstado());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoDto> actualizarPedido(
            @PathVariable Long id,
            @Valid @RequestBody PedidoDto pedidoDto) {
        return ResponseEntity.ok(pedidoService.actualizarPedido(id, pedidoDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelarPedido(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }
}