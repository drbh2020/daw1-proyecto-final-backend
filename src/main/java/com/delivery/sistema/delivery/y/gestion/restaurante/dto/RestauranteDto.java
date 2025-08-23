package com.delivery.sistema.delivery.y.gestion.restaurante.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteDto {
    private Long id;
    
    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;
    
    private String clienteNombre;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
    
    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;
    
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;
    
    private Boolean activo = true;
    
    @NotNull(message = "La hora de apertura es obligatoria")
    private LocalTime horaApertura;
    
    @NotNull(message = "La hora de cierre es obligatoria")
    private LocalTime horaCierre;
    
    private LocalDateTime fechaRegistro;
}