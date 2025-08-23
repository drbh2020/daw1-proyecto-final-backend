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
import java.util.Set;
import java.util.HashSet;

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

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "El teléfono debe tener formato válido")
    private String telefono;

    @Size(max = 50, message = "El vehículo no puede exceder 50 caracteres")
    private String vehiculo;

    @Column(nullable = false)
    private Boolean disponible = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRepartidor estado = EstadoRepartidor.LIBRE;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "repartidor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Entrega> entregas = new HashSet<>();
}
