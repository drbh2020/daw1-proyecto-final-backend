package com.delivery.sistema.delivery.y.gestion.pedido.dto;

import com.delivery.sistema.delivery.y.gestion.pedido.model.EstadoPedido;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDto {
    private Long id;
    
    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;
    
    private String clienteNombre;
    
    @NotNull(message = "El restaurante es obligatorio")
    private Long restauranteId;
    
    private String restauranteNombre;
    
    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccionEntrega;
    
    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;
    
    private Integer tiempoEstimado;
    
    @NotNull(message = "El costo de delivery es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El costo de delivery no puede ser negativo")
    private BigDecimal costoDelivery;
    
    @NotBlank(message = "El método de pago es obligatorio")
    @Size(max = 20, message = "El método de pago no puede exceder 20 caracteres")
    private String metodoPago;
    
    private EstadoPedido estado = EstadoPedido.PENDIENTE;
    
    private LocalDateTime fechaPedido;
    
    private LocalDateTime fechaActualizacion;
    
    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El total no puede ser negativo")
    private BigDecimal total;
    
    private List<DetallePedidoDto> detalles;
}