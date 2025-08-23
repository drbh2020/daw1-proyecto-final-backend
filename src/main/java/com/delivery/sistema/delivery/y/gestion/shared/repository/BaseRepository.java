package com.delivery.sistema.delivery.y.gestion.shared.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    @Query("SELECT e FROM #{#entityName} e WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<T> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.activo = :activo")
    Long countByActivo(@Param("activo") Boolean activo);

    @Query("SELECT e FROM #{#entityName} e WHERE e.fechaCreacion BETWEEN :fechaInicio AND :fechaFin ORDER BY e.fechaCreacion DESC")
    List<T> findByFechaCreacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT e FROM #{#entityName} e WHERE e.activo = :activo")
    Page<T> findByActivoWithPagination(@Param("activo") Boolean activo, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.nombre = :nombre AND e.activo = true")
    Boolean existsByNombreAndActivoTrue(@Param("nombre") String nombre);

    @Query("SELECT e FROM #{#entityName} e WHERE e.activo = :activo ORDER BY e.fechaCreacion DESC, e.nombre ASC")
    List<T> findByActivoOrderByFechaAndNombre(@Param("activo") Boolean activo);
}