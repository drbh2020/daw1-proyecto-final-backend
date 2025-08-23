package com.delivery.sistema.delivery.y.gestion.pedido.controller;

import com.delivery.sistema.delivery.y.gestion.pedido.dto.PedidoDto;
import com.delivery.sistema.delivery.y.gestion.pedido.dto.CrearPedidoDto;
import com.delivery.sistema.delivery.y.gestion.pedido.service.PedidoService;
import com.delivery.sistema.delivery.y.gestion.pedido.model.EstadoPedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Pedidos", description = "Gestión de pedidos del sistema")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar pedidos (Admin)", 
               description = "Obtiene una lista paginada de pedidos con filtros opcionales")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
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
    @Operation(summary = "Mis pedidos", 
               description = "Obtiene los pedidos del cliente autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos del cliente obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de cliente")
    })
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
    @Operation(summary = "Crear nuevo pedido", 
               description = "Permite a un cliente crear un nuevo pedido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de cliente")
    })
    public ResponseEntity<PedidoDto> crearPedido(@Valid @RequestBody CrearPedidoDto crearPedidoDto) {
        return ResponseEntity.ok(pedidoService.crearPedido(crearPedidoDto));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE') or hasRole('REPARTIDOR')")
    @Operation(summary = "Cambiar estado del pedido", 
               description = "Permite cambiar el estado de un pedido según el rol del usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado del pedido actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Estado inválido para la transición"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para cambiar el estado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<PedidoDto> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, estado));
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener estadísticas de pedidos", 
               description = "Obtiene un resumen de pedidos agrupados por estado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    public ResponseEntity<Map<String, Long>> obtenerEstadisticas() {
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