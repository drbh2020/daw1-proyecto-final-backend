package com.delivery.sistema.delivery.y.gestion.delivery.model;

import com.delivery.sistema.delivery.y.gestion.pedido.model.Pedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "entrega")
public class Entrega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repartidor_id", nullable = false)
    private Repartidor repartidor;

    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    @Column(precision = 10, scale = 8)
    private BigDecimal latitud;

    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    @Column(precision = 11, scale = 8)
    private BigDecimal longitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_entrega", nullable = false)
    private EstadoEntrega estadoEntrega = EstadoEntrega.ASIGNADO;

    @CreationTimestamp
    @Column(name = "fecha_inicio", nullable = false, updatable = false)
    private LocalDateTime fechaInicio;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "comentarios", length = 500)
    private String comentarios;

    // Campo adicional para fechas
    @CreationTimestamp
    @Column(name = "fecha_asignacion", nullable = false, updatable = false)
    private LocalDateTime fechaAsignacion;
}
