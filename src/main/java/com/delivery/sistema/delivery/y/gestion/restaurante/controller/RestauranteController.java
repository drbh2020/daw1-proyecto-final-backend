package com.delivery.sistema.delivery.y.gestion.restaurante.controller;

import com.delivery.sistema.delivery.y.gestion.restaurante.dto.RestauranteDto;
import com.delivery.sistema.delivery.y.gestion.restaurante.service.RestauranteService;
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
@RequestMapping("/api/restaurantes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Restaurantes", description = "Gestión de restaurantes del sistema")
@SecurityRequirement(name = "bearerAuth")
public class RestauranteController {

    private final RestauranteService restauranteService;

    @GetMapping
    @Operation(summary = "Listar restaurantes", 
               description = "Obtiene una lista paginada de restaurantes, opcionalmente filtrados por estado activo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de restaurantes obtenida exitosamente")
    })
    public ResponseEntity<Page<RestauranteDto>> listarRestaurantes(
            @RequestParam(required = false) Boolean activo,
            Pageable pageable) {
        if (activo != null) {
            return ResponseEntity.ok(restauranteService.listarRestaurantesActivos(pageable));
        }
        return ResponseEntity.ok(restauranteService.listarRestaurantes(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener restaurante por ID", 
               description = "Obtiene los detalles de un restaurante específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
        @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    public ResponseEntity<RestauranteDto> obtenerRestaurante(@PathVariable Long id) {
        return ResponseEntity.ok(restauranteService.obtenerRestaurantePorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE')")
    @Operation(summary = "Crear nuevo restaurante", 
               description = "Permite crear un nuevo restaurante en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurante creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para crear restaurante")
    })
    public ResponseEntity<RestauranteDto> crearRestaurante(@Valid @RequestBody RestauranteDto restauranteDto) {
        return ResponseEntity.ok(restauranteService.crearRestaurante(restauranteDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteService.esOwner(#id, authentication.name))")
    @Operation(summary = "Actualizar restaurante", 
               description = "Permite actualizar los datos de un restaurante existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurante actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para actualizar este restaurante"),
        @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    public ResponseEntity<RestauranteDto> actualizarRestaurante(
            @PathVariable Long id, 
            @Valid @RequestBody RestauranteDto restauranteDto) {
        return ResponseEntity.ok(restauranteService.actualizarRestaurante(id, restauranteDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar restaurante", 
               description = "Permite eliminar un restaurante del sistema (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Restaurante eliminado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para eliminar restaurante"),
        @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    public ResponseEntity<Void> eliminarRestaurante(@PathVariable Long id) {
        restauranteService.eliminarRestaurante(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/metricas")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteService.esOwner(#id, authentication.name))")
    @Operation(summary = "Obtener métricas del restaurante", 
               description = "Obtiene métricas y estadísticas básicas del restaurante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Métricas obtenidas exitosamente"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ver las métricas de este restaurante"),
        @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    public ResponseEntity<Map<String, Object>> obtenerMetricas(@PathVariable Long id) {
        return ResponseEntity.ok(restauranteService.obtenerMetricasBasicas());
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteService.esOwner(#id, authentication.name))")
    @Operation(summary = "Cambiar estado del restaurante", 
               description = "Permite activar o desactivar un restaurante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado del restaurante actualizado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para cambiar el estado de este restaurante"),
        @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    public ResponseEntity<RestauranteDto> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        return ResponseEntity.ok(restauranteService.cambiarEstado(id, activo));
    }
}