package com.delivery.sistema.delivery.y.gestion.shared.repository;

import com.delivery.sistema.delivery.y.gestion.shared.model.Calificacion;
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
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    List<Calificacion> findByRestauranteIdOrderByFechaCalificacionDesc(Long restauranteId);

    List<Calificacion> findByClienteIdOrderByFechaCalificacionDesc(Long clienteId);

    Optional<Calificacion> findByClienteIdAndRestauranteId(Long clienteId, Long restauranteId);

    @Query("SELECT AVG(c.puntuacion) FROM Calificacion c WHERE c.restaurante.id = :restauranteId")
    Optional<Double> calcularPromedioCalificacionRestaurante(@Param("restauranteId") Long restauranteId);

    @Query("SELECT COUNT(c) FROM Calificacion c WHERE c.restaurante.id = :restauranteId")
    Long countCalificacionesByRestaurante(@Param("restauranteId") Long restauranteId);

    @Query("SELECT c.puntuacion, COUNT(c) FROM Calificacion c WHERE c.restaurante.id = :restauranteId GROUP BY c.puntuacion ORDER BY c.puntuacion DESC")
    List<Object[]> getDistribucionCalificacionesByRestaurante(@Param("restauranteId") Long restauranteId);

    @Query("SELECT c FROM Calificacion c WHERE c.restaurante.id = :restauranteId AND c.puntuacion >= :puntuacionMinima ORDER BY c.fechaCalificacion DESC")
    List<Calificacion> findCalificacionesByRestauranteAndPuntuacionMinima(@Param("restauranteId") Long restauranteId, @Param("puntuacionMinima") Integer puntuacionMinima);

    @Query("SELECT c FROM Calificacion c WHERE c.comentario IS NOT NULL AND c.comentario != '' AND c.restaurante.id = :restauranteId ORDER BY c.fechaCalificacion DESC")
    List<Calificacion> findCalificacionesConComentarioByRestaurante(@Param("restauranteId") Long restauranteId);

    boolean existsByClienteIdAndRestauranteId(Long clienteId, Long restauranteId);

    @Query("SELECT c FROM Calificacion c LEFT JOIN FETCH c.cliente LEFT JOIN FETCH c.restaurante WHERE c.id = :calificacionId")
    Optional<Calificacion> findByIdWithDetails(@Param("calificacionId") Long calificacionId);

    // Métodos agregados para corregir discrepancias con CalificacionService
    
    List<Calificacion> findByPuntuacionOrderByFechaCalificacionDesc(Integer puntuacion);
    
    List<Calificacion> findTop10ByOrderByFechaCalificacionDesc();
    
    Page<Calificacion> findByRestauranteId(Long restauranteId, Pageable pageable);
    
    Optional<Calificacion> findByPedidoId(Long pedidoId);
    
    boolean existsByPedidoId(Long pedidoId);
    
    @Query("SELECT COALESCE(AVG(c.puntuacion), 0.0) FROM Calificacion c WHERE c.restaurante.id = :restauranteId")
    Optional<Double> calcularPromedioByRestaurante(@Param("restauranteId") Long restauranteId);
    
    Long countByRestauranteId(Long restauranteId);
    
    Long countByRestauranteIdAndPuntuacion(Long restauranteId, Integer puntuacion);
    
    @Query("SELECT c FROM Calificacion c WHERE c.restaurante.id = :restauranteId ORDER BY c.puntuacion DESC")
    List<Calificacion> findTopCalificacionesByRestaurante(@Param("restauranteId") Long restauranteId, @Param("limit") Integer limit);
    
    @Query("SELECT c FROM Calificacion c WHERE c.restaurante.id = :restauranteId ORDER BY c.fechaCalificacion DESC")
    List<Calificacion> findRecentCalificacionesByRestaurante(@Param("restauranteId") Long restauranteId, @Param("limit") Integer limit);
    
    Long countByClienteId(Long clienteId);
    
    @Query("SELECT c FROM Calificacion c WHERE c.fechaCalificacion BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaCalificacion DESC")
    List<Calificacion> findByFechaBetween(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT c FROM Calificacion c LEFT JOIN FETCH c.pedido LEFT JOIN FETCH c.cliente LEFT JOIN FETCH c.restaurante WHERE c.id = :calificacionId")
    Optional<Calificacion> findByIdWithPedidoAndClienteAndRestaurante(@Param("calificacionId") Long calificacionId);
}