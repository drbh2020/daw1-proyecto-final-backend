package com.delivery.sistema.delivery.y.gestion.shared.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "configuracion")
public class Configuracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La clave de configuración es obligatoria")
    @Size(max = 100, message = "La clave no puede exceder 100 caracteres")
    @Column(name = "config_key", nullable = false, unique = true)
    private String configKey;

    @NotBlank(message = "El valor de configuración es obligatorio")
    @Size(max = 500, message = "El valor no puede exceder 500 caracteres")
    @Column(name = "config_value", nullable = false)
    private String configValue;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}