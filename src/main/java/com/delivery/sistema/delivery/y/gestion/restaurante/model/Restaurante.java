package com.delivery.sistema.delivery.y.gestion.restaurante.model;

import com.delivery.sistema.delivery.y.gestion.cliente.model.Cliente;
import com.delivery.sistema.delivery.y.gestion.pedido.model.Pedido;
import com.delivery.sistema.delivery.y.gestion.shared.model.Promocion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurante")
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    @Column(name = "direccion", nullable = false, length = 255)
    private String direccion;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @NotNull(message = "La hora de apertura es obligatoria")
    @Column(name = "hora_apertura", nullable = false)
    private LocalTime horaApertura;

    @NotNull(message = "La hora de cierre es obligatoria")
    @Column(name = "hora_cierre", nullable = false)
    private LocalTime horaCierre;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Menu> menus = new HashSet<>();

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Pedido> pedidos = new HashSet<>();

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Promocion> promociones = new HashSet<>();
}
