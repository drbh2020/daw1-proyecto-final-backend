package com.delivery.sistema.delivery.y.gestion.shared.repository;

import com.delivery.sistema.delivery.y.gestion.shared.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    List<Promocion> findByActivoTrueOrderByFechaCreacionDesc();

    List<Promocion> findByRestauranteIdAndActivoTrueOrderByFechaCreacionDesc(Long restauranteId);

    @Query("SELECT p FROM Promocion p WHERE p.activo = true AND p.fechaInicio <= :ahora AND p.fechaFin >= :ahora ORDER BY p.fechaCreacion DESC")
    List<Promocion> findPromocionesVigentes(@Param("ahora") LocalDateTime ahora);

    @Query("SELECT p FROM Promocion p WHERE p.restaurante.id = :restauranteId AND p.activo = true AND p.fechaInicio <= :ahora AND p.fechaFin >= :ahora ORDER BY p.fechaCreacion DESC")
    List<Promocion> findPromocionesVigentesByRestaurante(@Param("restauranteId") Long restauranteId, @Param("ahora") LocalDateTime ahora);

    @Query("SELECT p FROM Promocion p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.activo = true")
    List<Promocion> findByNombreContainingIgnoreCaseAndActivoTrue(@Param("nombre") String nombre);

    @Query("SELECT COUNT(p) FROM Promocion p WHERE p.restaurante.id = :restauranteId AND p.activo = true")
    Long countPromocionesActivasByRestaurante(@Param("restauranteId") Long restauranteId);

    @Query("SELECT p FROM Promocion p WHERE p.fechaFin < :ahora AND p.activo = true")
    List<Promocion> findPromocionesVencidas(@Param("ahora") LocalDateTime ahora);

    @Query("SELECT p FROM Promocion p WHERE p.fechaInicio BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaInicio ASC")
    List<Promocion> findPromocionesByRangoFechas(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    boolean existsByRestauranteIdAndNombreAndActivoTrue(Long restauranteId, String nombre);
}