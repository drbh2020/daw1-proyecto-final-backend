package com.delivery.sistema.delivery.y.gestion.pedido.repository;

import com.delivery.sistema.delivery.y.gestion.pedido.model.Pedido;
import com.delivery.sistema.delivery.y.gestion.pedido.model.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteIdOrderByFechaPedidoDesc(Long clienteId);

    List<Pedido> findByRestauranteIdOrderByFechaPedidoDesc(Long restauranteId);

    List<Pedido> findByEstadoOrderByFechaPedidoAsc(EstadoPedido estado);

    Page<Pedido> findByClienteId(Long clienteId, Pageable pageable);

    Page<Pedido> findByRestauranteId(Long restauranteId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.restaurante.id = :restauranteId AND p.estado = :estado ORDER BY p.fechaPedido ASC")
    List<Pedido> findPedidosByRestauranteAndEstado(@Param("restauranteId") Long restauranteId, @Param("estado") EstadoPedido estado);

    @Query("SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId AND p.fechaPedido BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaPedido DESC")
    List<Pedido> findPedidosByClienteAndFechas(@Param("clienteId") Long clienteId, @Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT p FROM Pedido p WHERE p.restaurante.id = :restauranteId AND p.fechaPedido BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaPedido DESC")
    List<Pedido> findPedidosByRestauranteAndFechas(@Param("restauranteId") Long restauranteId, @Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.restaurante.id = :restauranteId AND p.estado = :estado")
    Long countPedidosByRestauranteAndEstado(@Param("restauranteId") Long restauranteId, @Param("estado") EstadoPedido estado);

    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.restaurante.id = :restauranteId AND p.estado = 'ENTREGADO' AND p.fechaPedido BETWEEN :fechaInicio AND :fechaFin")
    Optional<Double> calcularVentasByRestauranteAndFechas(@Param("restauranteId") Long restauranteId, @Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT p FROM Pedido p WHERE p.estado IN ('PENDIENTE', 'CONFIRMADO', 'EN_PREPARACION', 'LISTO') ORDER BY p.fechaPedido ASC")
    List<Pedido> findPedidosActivos();

    boolean existsByClienteIdAndRestauranteIdAndEstado(Long clienteId, Long restauranteId, EstadoPedido estado);

    // Métodos agregados para corregir discrepancias con PedidoService
    
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    Long countPedidosByEstado(@Param("estado") EstadoPedido estado);

    boolean existsByIdAndClienteIdAndEstado(Long pedidoId, Long clienteId, EstadoPedido estado);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.fechaPedido BETWEEN :fechaInicio AND :fechaFin")
    Long countPedidosByFechas(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.estado = 'ENTREGADO' AND p.fechaPedido BETWEEN :fechaInicio AND :fechaFin")
    Optional<Double> calcularVentasByFechas(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.restaurante.id = :restauranteId AND p.fechaPedido BETWEEN :fechaInicio AND :fechaFin")
    Long countPedidosByRestauranteAndFechas(@Param("restauranteId") Long restauranteId, @Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    // Métodos adicionales con Pageable requeridos por PedidoService
    Page<Pedido> findByRestauranteIdAndEstado(Long restauranteId, EstadoPedido estado, Pageable pageable);
    
    Page<Pedido> findByEstado(EstadoPedido estado, Pageable pageable);
}