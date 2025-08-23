package com.delivery.sistema.delivery.y.gestion.delivery.dto;

import com.delivery.sistema.delivery.y.gestion.delivery.model.EstadoEntrega;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntregaDto {
    private Long id;
    
    @NotNull(message = "El pedido es obligatorio")
    private Long pedidoId;
    
    @NotNull(message = "El repartidor es obligatorio")
    private Long repartidorId;
    
    private String repartidorNombre;
    
    private String repartidorVehiculo;
    
    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    private BigDecimal latitud;
    
    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    private BigDecimal longitud;
    
    private EstadoEntrega estadoEntrega = EstadoEntrega.ASIGNADO;
    
    private LocalDateTime fechaInicio;
    
    private LocalDateTime fechaActualizacion;
    
    private LocalDateTime fechaEntrega;
    
    private String comentarios;
}