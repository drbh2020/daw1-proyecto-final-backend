package com.delivery.sistema.delivery.y.gestion.shared.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionDto {
    private Long id;
    
    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private Integer puntuacion;
    
    @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
    private String comentario;
    
    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;
    
    private String clienteNombre;
    
    @NotNull(message = "El restaurante es obligatorio")
    private Long restauranteId;
    
    private String restauranteNombre;
    
    private Long pedidoId;
    
    private LocalDateTime fechaCalificacion;
}