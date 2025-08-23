package com.delivery.sistema.delivery.y.gestion.delivery.repository;

import com.delivery.sistema.delivery.y.gestion.delivery.model.Entrega;
import com.delivery.sistema.delivery.y.gestion.delivery.model.EstadoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    Optional<Entrega> findByPedidoId(Long pedidoId);

    List<Entrega> findByRepartidorId(Long repartidorId);

    List<Entrega> findByEstadoEntrega(EstadoEntrega estadoEntrega);
    
    Page<Entrega> findByEstadoEntrega(EstadoEntrega estadoEntrega, Pageable pageable);

    List<Entrega> findByRepartidorIdAndEstadoEntrega(Long repartidorId, EstadoEntrega estadoEntrega);
    
    Page<Entrega> findByRepartidorIdAndEstadoEntrega(Long repartidorId, EstadoEntrega estadoEntrega, Pageable pageable);

    @Query("SELECT e FROM Entrega e WHERE e.repartidor.id = :repartidorId ORDER BY e.fechaInicio DESC")
    List<Entrega> findEntregasByRepartidorOrdenadas(@Param("repartidorId") Long repartidorId);

    @Query("SELECT e FROM Entrega e WHERE e.estadoEntrega = 'EN_CAMINO' ORDER BY e.fechaInicio ASC")
    List<Entrega> findEntregasEnCamino();

    @Query("SELECT e FROM Entrega e WHERE e.fechaInicio BETWEEN :fechaInicio AND :fechaFin ORDER BY e.fechaInicio DESC")
    List<Entrega> findEntregasByFechas(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT e FROM Entrega e WHERE e.repartidor.id = :repartidorId AND e.fechaInicio BETWEEN :fechaInicio AND :fechaFin ORDER BY e.fechaInicio DESC")
    List<Entrega> findEntregasByRepartidorAndFechas(@Param("repartidorId") Long repartidorId, @Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COUNT(e) FROM Entrega e WHERE e.repartidor.id = :repartidorId AND e.estadoEntrega = 'ENTREGADO'")
    Long countEntregasCompletadasByRepartidor(@Param("repartidorId") Long repartidorId);

    @Query("SELECT COUNT(e) FROM Entrega e WHERE e.estadoEntrega = :estado")
    Long countByEstadoEntrega(@Param("estado") EstadoEntrega estado);

    @Query("SELECT e FROM Entrega e LEFT JOIN FETCH e.pedido LEFT JOIN FETCH e.repartidor WHERE e.id = :entregaId")
    Optional<Entrega> findByIdWithDetails(@Param("entregaId") Long entregaId);

    boolean existsByPedidoId(Long pedidoId);

    // Métodos agregados para corregir discrepancias con EntregaService
    
    List<Entrega> findByEstadoEntregaOrderByFechaAsignacionAsc(EstadoEntrega estado);
    
    List<Entrega> findByRepartidorIdOrderByFechaAsignacionDesc(Long repartidorId);
    
    @Query("SELECT e FROM Entrega e WHERE e.estadoEntrega IN ('ASIGNADO', 'EN_CAMINO') ORDER BY e.fechaAsignacion ASC")
    List<Entrega> findEntregasActivas();
    
    @Query("SELECT e FROM Entrega e WHERE e.estadoEntrega IN ('ASIGNADO', 'EN_CAMINO') ORDER BY e.fechaAsignacion ASC")
    Page<Entrega> findEntregasActivas(Pageable pageable);
    
    Page<Entrega> findByRepartidorId(Long repartidorId, Pageable pageable);
    
    Long countByRepartidorId(Long repartidorId);
    
    @Query("SELECT COUNT(e) FROM Entrega e WHERE e.repartidor.id = :repartidorId AND e.estadoEntrega = 'ENTREGADO' AND e.fechaEntrega BETWEEN :fechaInicio AND :fechaFin")
    Long countEntregasCompletadasByRepartidorAndFechas(@Param("repartidorId") Long repartidorId, @Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT e FROM Entrega e WHERE e.repartidor.id = :repartidorId AND e.fechaEntrega BETWEEN :fechaInicio AND :fechaFin ORDER BY e.fechaEntrega DESC")
    List<Entrega> findByRepartidorIdAndFechaEntregaBetween(@Param("repartidorId") Long repartidorId, @Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT e FROM Entrega e LEFT JOIN FETCH e.pedido LEFT JOIN FETCH e.repartidor WHERE e.id = :entregaId")
    Optional<Entrega> findByIdWithPedidoAndRepartidor(@Param("entregaId") Long entregaId);
}