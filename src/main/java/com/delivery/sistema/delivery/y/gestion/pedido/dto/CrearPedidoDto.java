package com.delivery.sistema.delivery.y.gestion.pedido.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearPedidoDto {
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;
    
    @NotNull(message = "El ID del restaurante es obligatorio")
    private Long restauranteId;
    
    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccionEntrega;
    
    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;
    
    private String metodoPago = "EFECTIVO";
    
    @NotNull(message = "Los detalles del pedido son obligatorios")
    private List<DetallePedidoDto> detalles;
}