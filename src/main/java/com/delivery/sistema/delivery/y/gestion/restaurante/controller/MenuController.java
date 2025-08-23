package com.delivery.sistema.delivery.y.gestion.restaurante.controller;

import com.delivery.sistema.delivery.y.gestion.restaurante.dto.MenuDto;
import com.delivery.sistema.delivery.y.gestion.restaurante.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<Page<MenuDto>> listarMenus(
            @RequestParam(required = false) Long restauranteId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Boolean disponible,
            Pageable pageable) {
        
        if (restauranteId != null) {
            if (disponible != null && disponible) {
                return ResponseEntity.ok(menuService.listarMenusDisponiblesPorRestaurante(restauranteId, pageable));
            }
            return ResponseEntity.ok(menuService.listarMenusPorRestaurante(restauranteId, pageable));
        }
        
        if (categoriaId != null) {
            return ResponseEntity.ok(menuService.listarMenusPorCategoria(categoriaId, pageable));
        }
        
        return ResponseEntity.ok(menuService.listarMenus(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuDto> obtenerMenu(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.obtenerMenuPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE')")
    public ResponseEntity<MenuDto> crearMenu(@Valid @RequestBody MenuDto menuDto) {
        return ResponseEntity.ok(menuService.crearMenu(menuDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @menuService.esOwnerDelMenu(#id, authentication.name))")
    public ResponseEntity<MenuDto> actualizarMenu(
            @PathVariable Long id, 
            @Valid @RequestBody MenuDto menuDto) {
        return ResponseEntity.ok(menuService.actualizarMenu(id, menuDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @menuService.esOwnerDelMenu(#id, authentication.name))")
    public ResponseEntity<Void> eliminarMenu(@PathVariable Long id) {
        menuService.eliminarMenu(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/imagen")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @menuService.esOwnerDelMenu(#id, authentication.name))")
    public ResponseEntity<String> subirImagen(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = menuService.subirImagen(id, file);
        return ResponseEntity.ok(imageUrl);
    }

    @PatchMapping("/{id}/disponibilidad")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @menuService.esOwnerDelMenu(#id, authentication.name))")
    public ResponseEntity<MenuDto> cambiarDisponibilidad(
            @PathVariable Long id, 
            @RequestParam boolean disponible) {
        return ResponseEntity.ok(menuService.cambiarDisponibilidad(id, disponible));
    }
}