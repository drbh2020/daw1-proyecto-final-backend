package com.delivery.sistema.delivery.y.gestion.shared.model;

import com.delivery.sistema.delivery.y.gestion.cliente.model.Cliente;
import com.delivery.sistema.delivery.y.gestion.restaurante.model.Restaurante;
import com.delivery.sistema.delivery.y.gestion.pedido.model.Pedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "calificacion", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cliente_id", "restaurante_id"})
})
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    @Column(nullable = false)
    private Integer puntuacion;

    @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
    private String comentario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = true)
    private Pedido pedido;

    @CreationTimestamp
    @Column(name = "fecha_calificacion", nullable = false, updatable = false)
    private LocalDateTime fechaCalificacion;
}
