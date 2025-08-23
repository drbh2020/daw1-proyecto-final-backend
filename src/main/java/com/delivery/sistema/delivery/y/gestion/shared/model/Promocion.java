package com.delivery.sistema.delivery.y.gestion.shared.model;

import com.delivery.sistema.delivery.y.gestion.restaurante.model.Restaurante;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "promocion")
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El restaurante es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @NotBlank(message = "El nombre de la promoción es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @NotNull(message = "El tipo de promoción es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPromocion tipo;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El valor no puede ser negativo")
    @Column(precision = 8, scale = 2, nullable = false)
    private BigDecimal valor;

    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    @Column(unique = true, length = 20)
    private String codigo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @DecimalMin(value = "0.0", message = "El monto mínimo no puede ser negativo")
    @Column(name = "monto_minimo", precision = 10, scale = 2)
    private BigDecimal montoMinimo = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "usos_maximos")
    private Integer usosMaximos;

    @Column(name = "usos_actuales", nullable = false)
    private Integer usosActuales = 0;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    // https://medium.com/@nweligalla/applying-prepersist-and-preupdate-in-jpa-8e6b621fc51a
    // https://www.baeldung.com/jpa-entity-lifecycle-events
    @PrePersist
    @PreUpdate
    private void validarFechas() {
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
    }
}