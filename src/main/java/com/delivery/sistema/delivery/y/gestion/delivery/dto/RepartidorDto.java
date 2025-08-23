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
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "El teléfono debe tener formato válido")
    private String telefono;
    
    @Size(max = 50, message = "El vehículo no puede exceder 50 caracteres")
    private String vehiculo;
    
    private Boolean disponible = true;
    
    private EstadoRepartidor estado = EstadoRepartidor.LIBRE;
    
    private LocalDateTime fechaRegistro;
    
    private LocalDateTime fechaActualizacion;
}