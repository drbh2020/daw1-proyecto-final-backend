package com.delivery.sistema.delivery.y.gestion.restaurante.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Digits(integer = 6, fraction = 2, message = "El precio debe tener máximo 6 dígitos enteros y 2 decimales")
    private BigDecimal precio;
    
    @Size(max = 500, message = "La URL de imagen no puede exceder 500 caracteres")
    private String imagenUrl;
    
    private Boolean disponible = true;
    
    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;
    
    private String categoriaNombre;
    
    @NotNull(message = "El restaurante es obligatorio")
    private Long restauranteId;
    
    private String restauranteNombre;
    
    private LocalDateTime fechaCreacion;
    
    private LocalDateTime fechaActualizacion;
}