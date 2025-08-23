package com.delivery.sistema.delivery.y.gestion.restaurante.controller;

import com.delivery.sistema.delivery.y.gestion.restaurante.dto.MenuDto;
import com.delivery.sistema.delivery.y.gestion.restaurante.service.MenuService;
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
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Menús", description = "Gestión de menús y platos de restaurantes")
@SecurityRequirement(name = "bearerAuth")
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    @Operation(summary = "Listar menús", 
               description = "Obtiene una lista paginada de menús, opcionalmente filtrados por restaurante, categoría y disponibilidad")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de menús obtenida exitosamente")
    })
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
    @Operation(summary = "Obtener menú por ID", 
               description = "Obtiene los detalles de un menú específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menú encontrado"),
        @ApiResponse(responseCode = "404", description = "Menú no encontrado")
    })
    public ResponseEntity<MenuDto> obtenerMenu(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.obtenerMenuPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE')")
    @Operation(summary = "Crear nuevo menú", 
               description = "Permite crear un nuevo menú o plato en el restaurante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menú creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para crear menú")
    })
    public ResponseEntity<MenuDto> crearMenu(@Valid @RequestBody MenuDto menuDto) {
        return ResponseEntity.ok(menuService.crearMenu(menuDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @menuService.esOwnerDelMenu(#id, authentication.name))")
    @Operation(summary = "Actualizar menú", 
               description = "Permite actualizar los datos de un menú existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menú actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para actualizar este menú"),
        @ApiResponse(responseCode = "404", description = "Menú no encontrado")
    })
    public ResponseEntity<MenuDto> actualizarMenu(
            @PathVariable Long id, 
            @Valid @RequestBody MenuDto menuDto) {
        return ResponseEntity.ok(menuService.actualizarMenu(id, menuDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @menuService.esOwnerDelMenu(#id, authentication.name))")
    @Operation(summary = "Eliminar menú", 
               description = "Permite eliminar un menú del catálogo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Menú eliminado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para eliminar este menú"),
        @ApiResponse(responseCode = "404", description = "Menú no encontrado")
    })
    public ResponseEntity<Void> eliminarMenu(@PathVariable Long id) {
        menuService.eliminarMenu(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/imagen")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @menuService.esOwnerDelMenu(#id, authentication.name))")
    @Operation(summary = "Subir imagen del menú", 
               description = "Permite subir una imagen para un menú específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagen subida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Archivo de imagen inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para subir imagen a este menú"),
        @ApiResponse(responseCode = "404", description = "Menú no encontrado")
    })
    public ResponseEntity<String> subirImagen(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = menuService.subirImagen(id, file);
        return ResponseEntity.ok(imageUrl);
    }

    @PatchMapping("/{id}/disponibilidad")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @menuService.esOwnerDelMenu(#id, authentication.name))")
    @Operation(summary = "Cambiar disponibilidad del menú", 
               description = "Permite activar o desactivar la disponibilidad de un menú")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada exitosamente"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para cambiar la disponibilidad de este menú"),
        @ApiResponse(responseCode = "404", description = "Menú no encontrado")
    })
    public ResponseEntity<MenuDto> cambiarDisponibilidad(
            @PathVariable Long id, 
            @RequestParam boolean disponible) {
        return ResponseEntity.ok(menuService.cambiarDisponibilidad(id, disponible));
    }
}