package com.delivery.sistema.delivery.y.gestion.delivery.repository;

import com.delivery.sistema.delivery.y.gestion.delivery.model.Repartidor;
import com.delivery.sistema.delivery.y.gestion.delivery.model.EstadoRepartidor;
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
public interface RepartidorRepository extends JpaRepository<Repartidor, Long> {

    List<Repartidor> findByEstado(EstadoRepartidor estado);

    List<Repartidor> findByEstadoOrderByFechaRegistroAsc(EstadoRepartidor estado);

    Optional<Repartidor> findByClienteId(Long clienteId);

    @Query("SELECT r FROM Repartidor r WHERE r.estado = 'LIBRE' ORDER BY r.fechaRegistro ASC")
    List<Repartidor> findRepartidoresDisponibles();

    @Query("SELECT r FROM Repartidor r WHERE r.estado = 'OCUPADO' ORDER BY r.fechaActualizacion DESC")
    List<Repartidor> findRepartidoresOcupados();

    @Query("SELECT COUNT(r) FROM Repartidor r WHERE r.estado = :estado")
    Long countByEstado(@Param("estado") EstadoRepartidor estado);

    @Query("SELECT r FROM Repartidor r WHERE r.cliente.email = :email")
    Optional<Repartidor> findByClienteEmail(@Param("email") String email);

    @Query("SELECT r FROM Repartidor r JOIN r.cliente c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Repartidor> findByClienteNombreContainingIgnoreCase(@Param("nombre") String nombre);

    @Query("SELECT r FROM Repartidor r WHERE r.vehiculo LIKE %:vehiculo%")
    List<Repartidor> findByVehiculoContaining(@Param("vehiculo") String vehiculo);

    boolean existsByClienteId(Long clienteId);

    @Query("SELECT r FROM Repartidor r LEFT JOIN FETCH r.cliente WHERE r.id = :repartidorId")
    Optional<Repartidor> findByIdWithCliente(@Param("repartidorId") Long repartidorId);

    // Métodos agregados para corregir discrepancias con RepartidorService
    
    @Query("SELECT r FROM Repartidor r WHERE r.disponible = true AND r.estado != 'INACTIVO' ORDER BY r.fechaRegistro ASC")
    List<Repartidor> findRepartidoresActivos();

    @Query("SELECT r FROM Repartidor r WHERE r.disponible = true AND r.estado = :estado ORDER BY r.fechaRegistro ASC")
    List<Repartidor> findByDisponibleTrueAndEstadoOrderByFechaRegistroAsc(@Param("estado") EstadoRepartidor estado);

    Page<Repartidor> findByDisponibleTrue(Pageable pageable);

    @Query("SELECT COUNT(e) FROM Entrega e WHERE e.repartidor.id = :repartidorId AND e.estadoEntrega IN ('ASIGNADO', 'EN_CAMINO')")
    Long countEntregasActivasByRepartidor(@Param("repartidorId") Long repartidorId);

    @Query("SELECT COUNT(r) FROM Repartidor r WHERE r.disponible = true")
    Long countByDisponibleTrue();

    @Query("SELECT COUNT(e) FROM Entrega e WHERE e.repartidor.id = :repartidorId AND e.estadoEntrega = 'ENTREGADO'")
    Long countEntregasCompletadasByRepartidor(@Param("repartidorId") Long repartidorId);

    @Query("SELECT r FROM Repartidor r LEFT JOIN FETCH r.entregas WHERE r.id = :repartidorId")
    Optional<Repartidor> findByIdWithEntregas(@Param("repartidorId") Long repartidorId);

    List<Repartidor> findByVehiculoContainingIgnoreCase(String vehiculo);

    @Query("SELECT COUNT(e) FROM Entrega e WHERE e.repartidor.id = :repartidorId AND e.fechaAsignacion BETWEEN :fechaInicio AND :fechaFin")
    Long countEntregasByRepartidorAndFechas(@Param("repartidorId") Long repartidorId, @Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COUNT(e) FROM Entrega e WHERE e.repartidor.id = :repartidorId AND e.estadoEntrega = 'FALLIDO' AND e.fechaAsignacion BETWEEN :fechaInicio AND :fechaFin")
    Long countEntregasFallidasByRepartidorAndFechas(@Param("repartidorId") Long repartidorId, @Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
}