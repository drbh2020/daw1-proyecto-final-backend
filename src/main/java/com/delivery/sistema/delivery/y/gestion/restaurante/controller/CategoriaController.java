package com.delivery.sistema.delivery.y.gestion.restaurante.controller;

import com.delivery.sistema.delivery.y.gestion.restaurante.service.CategoriaService;
import com.delivery.sistema.delivery.y.gestion.restaurante.dto.CategoriaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<?> listarCategorias(
            @RequestParam(required = false) Boolean activo,
            Pageable pageable) {
        if (activo != null && activo) {
            return ResponseEntity.ok(categoriaService.listarCategoriasActivas(pageable));
        }
        return ResponseEntity.ok(categoriaService.listarCategorias(pageable));
    }

    @GetMapping("/ordenadas")
    public ResponseEntity<?> listarCategoriasOrdenadas() {
        return ResponseEntity.ok(categoriaService.listarCategoriasOrdenadas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtenerCategoriaPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaDto> crearCategoria(@Valid @RequestBody CategoriaDto categoriaDto) {
        return ResponseEntity.ok(categoriaService.crearCategoria(categoriaDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaDto> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaDto categoriaDto) {
        return ResponseEntity.ok(categoriaService.actualizarCategoria(id, categoriaDto));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        return ResponseEntity.ok(categoriaService.cambiarEstado(id, activo));
    }

    @PatchMapping("/{id}/orden")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cambiarOrden(@PathVariable Long id, @RequestParam int orden) {
        return ResponseEntity.ok(categoriaService.cambiarOrden(id, orden));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}