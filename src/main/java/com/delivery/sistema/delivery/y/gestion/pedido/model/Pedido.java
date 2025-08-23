package com.delivery.sistema.delivery.y.gestion.pedido.model;

import com.delivery.sistema.delivery.y.gestion.cliente.model.Cliente;
import com.delivery.sistema.delivery.y.gestion.restaurante.model.Restaurante;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    @Column(name = "direccion_entrega", nullable = false)
    private String direccionEntrega;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;

    @Column(name = "tiempo_estimado")
    private Integer tiempoEstimado;

    @NotNull(message = "El costo de delivery es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El costo de delivery no puede ser negativo")
    @Column(name = "costo_delivery", precision = 10, scale = 2)
    private BigDecimal costoDelivery;

    @NotBlank(message = "El método de pago es obligatorio")
    @Size(max = 20, message = "El método de pago no puede exceder 20 caracteres")
    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @CreationTimestamp
    @Column(name = "fecha_pedido", nullable = false, updatable = false)
    private LocalDateTime fechaPedido;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El total no puede ser negativo")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<DetallePedido> detalles = new HashSet<>();
}
