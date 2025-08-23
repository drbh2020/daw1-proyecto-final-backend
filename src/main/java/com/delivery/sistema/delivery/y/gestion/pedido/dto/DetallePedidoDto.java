package com.delivery.sistema.delivery.y.gestion.pedido.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoDto {
    private Long id;
    
    @NotNull(message = "El pedido es obligatorio")
    private Long pedidoId;
    
    @NotNull(message = "El menú es obligatorio")
    private Long menuId;
    
    private String menuNombre;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio unitario no puede ser negativo")
    private BigDecimal precioUnitario;
    
    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El subtotal no puede ser negativo")
    private BigDecimal subtotal;
}