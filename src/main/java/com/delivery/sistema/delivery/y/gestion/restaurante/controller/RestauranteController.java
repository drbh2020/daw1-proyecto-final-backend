package com.delivery.sistema.delivery.y.gestion.restaurante.controller;

import com.delivery.sistema.delivery.y.gestion.restaurante.dto.RestauranteDto;
import com.delivery.sistema.delivery.y.gestion.restaurante.service.RestauranteService;
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
public class RestauranteController {

    private final RestauranteService restauranteService;

    @GetMapping
    public ResponseEntity<Page<RestauranteDto>> listarRestaurantes(
            @RequestParam(required = false) Boolean activo,
            Pageable pageable) {
        if (activo != null) {
            return ResponseEntity.ok(restauranteService.listarRestaurantesActivos(pageable));
        }
        return ResponseEntity.ok(restauranteService.listarRestaurantes(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestauranteDto> obtenerRestaurante(@PathVariable Long id) {
        return ResponseEntity.ok(restauranteService.obtenerRestaurantePorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE')")
    public ResponseEntity<RestauranteDto> crearRestaurante(@Valid @RequestBody RestauranteDto restauranteDto) {
        return ResponseEntity.ok(restauranteService.crearRestaurante(restauranteDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteService.esOwner(#id, authentication.name))")
    public ResponseEntity<RestauranteDto> actualizarRestaurante(
            @PathVariable Long id, 
            @Valid @RequestBody RestauranteDto restauranteDto) {
        return ResponseEntity.ok(restauranteService.actualizarRestaurante(id, restauranteDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarRestaurante(@PathVariable Long id) {
        restauranteService.eliminarRestaurante(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/metricas")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteService.esOwner(#id, authentication.name))")
    public ResponseEntity<Map<String, Object>> obtenerMetricas(@PathVariable Long id) {
        return ResponseEntity.ok(restauranteService.obtenerMetricasBasicas());
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteService.esOwner(#id, authentication.name))")
    public ResponseEntity<RestauranteDto> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        return ResponseEntity.ok(restauranteService.cambiarEstado(id, activo));
    }
}