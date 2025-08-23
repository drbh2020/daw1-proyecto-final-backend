package com.delivery.sistema.delivery.y.gestion.restaurante.repository;

import com.delivery.sistema.delivery.y.gestion.restaurante.model.Restaurante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

    List<Restaurante> findByActivoTrueOrderByNombreAsc();

    List<Restaurante> findByNombreContainingIgnoreCase(String nombre);

    List<Restaurante> findByClienteId(Long clienteId);

    Page<Restaurante> findByActivoTrue(Pageable pageable);

    Page<Restaurante> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre, Pageable pageable);

    @Query("SELECT r FROM Restaurante r WHERE r.activo = true AND r.horaApertura <= :horaActual AND r.horaCierre >= :horaActual")
    List<Restaurante> findRestaurantesAbiertos(@Param("horaActual") LocalTime horaActual);

    @Query("SELECT r FROM Restaurante r WHERE LOWER(r.direccion) LIKE LOWER(CONCAT('%', :direccion, '%')) AND r.activo = true")
    List<Restaurante> findByDireccionContainingIgnoreCaseAndActivoTrue(@Param("direccion") String direccion);

    @Query("SELECT COUNT(r) FROM Restaurante r WHERE r.activo = :activo")
    Long countByActivo(@Param("activo") Boolean activo);

    @Query("SELECT r FROM Restaurante r LEFT JOIN FETCH r.menus WHERE r.id = :restauranteId")
    Optional<Restaurante> findByIdWithMenus(@Param("restauranteId") Long restauranteId);

    @Query("SELECT r FROM Restaurante r WHERE r.id = :restauranteId AND r.activo = true AND r.horaApertura <= CURRENT_TIME AND r.horaCierre >= CURRENT_TIME")
    boolean isRestauranteAbierto(@Param("restauranteId") Long restauranteId);

    @Query("SELECT r FROM Restaurante r WHERE r.telefono = :telefono")
    Optional<Restaurante> findByTelefono(@Param("telefono") String telefono);

    @Query("SELECT AVG(SIZE(r.menus)) FROM Restaurante r WHERE r.activo = true")
    Double calcularPromedioMenusPorRestaurante();

    boolean existsByClienteId(Long clienteId);

    // Métodos agregados para corregir discrepancias con RestauranteService
    
    @Query("SELECT COUNT(r) FROM Restaurante r WHERE r.activo = true")
    Long countByActivoTrue();

    @Query("SELECT COUNT(m) FROM Menu m WHERE m.restaurante.id = :restauranteId")
    Long countMenusByRestaurante(@Param("restauranteId") Long restauranteId);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.restaurante.id = :restauranteId")
    Long countPedidosByRestaurante(@Param("restauranteId") Long restauranteId);

    @Query("SELECT COALESCE(AVG(c.puntuacion), 0.0) FROM Calificacion c WHERE c.restaurante.id = :restauranteId")
    Double calcularPromedioCalificaciones(@Param("restauranteId") Long restauranteId);
}
