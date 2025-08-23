package com.delivery.sistema.delivery.y.gestion.delivery.dto;

import com.delivery.sistema.delivery.y.gestion.delivery.model.EstadoRepartidor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepartidorDto {
    private Long id;
    
    private Long clienteId;
    
    private String clienteNombre;
    
    private String clienteEmail;
    
    @NotBlank(message = "El vehículo es obligatorio")
    @Size(max = 50, message = "El vehículo no puede exceder 50 caracteres")
    private String vehiculo;
    
    @Pattern(regexp = "^[A-Z0-9]{6,8}$", message = "La placa debe tener formato válido")
    @Size(max = 8, message = "La placa no puede exceder 8 caracteres")
    private String placa;
    
    private EstadoRepartidor estado = EstadoRepartidor.LIBRE;
    
    private LocalDateTime fechaRegistro;
    
    private LocalDateTime fechaActualizacion;
}