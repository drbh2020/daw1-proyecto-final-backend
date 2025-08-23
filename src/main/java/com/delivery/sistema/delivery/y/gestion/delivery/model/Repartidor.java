package com.delivery.sistema.delivery.y.gestion.delivery.model;

import com.delivery.sistema.delivery.y.gestion.cliente.model.Cliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "repartidor")
public class Repartidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotBlank(message = "El vehículo es obligatorio")
    @Size(max = 50, message = "El vehículo no puede exceder 50 caracteres")
    private String vehiculo;

    @Pattern(regexp = "^[A-Z0-9]{6,8}$", message = "La placa debe tener formato válido")
    @Size(max = 8, message = "La placa no puede exceder 8 caracteres")
    private String placa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRepartidor estado = EstadoRepartidor.LIBRE;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
