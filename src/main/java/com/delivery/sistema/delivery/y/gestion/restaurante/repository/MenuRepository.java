package com.delivery.sistema.delivery.y.gestion.restaurante.repository;

import com.delivery.sistema.delivery.y.gestion.restaurante.model.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByDisponibleTrueOrderByNombreAsc();

    List<Menu> findByRestauranteIdOrderByNombreAsc(Long restauranteId);

    List<Menu> findByRestauranteIdAndDisponibleTrueOrderByNombreAsc(Long restauranteId);

    List<Menu> findByCategoriaIdOrderByNombreAsc(Long categoriaId);

    List<Menu> findByNombreContainingIgnoreCase(String nombre);

    Page<Menu> findByRestauranteId(Long restauranteId, Pageable pageable);

    Page<Menu> findByDisponibleTrue(Pageable pageable);

    @Query("SELECT m FROM Menu m WHERE m.precio BETWEEN :precioMin AND :precioMax ORDER BY m.precio ASC")
    List<Menu> findByPrecioBetweenOrderByPrecioAsc(@Param("precioMin") BigDecimal precioMin, @Param("precioMax") BigDecimal precioMax);

    @Query("SELECT m FROM Menu m WHERE m.restaurante.id = :restauranteId AND m.categoria.id = :categoriaId AND m.disponible = true")
    List<Menu> findByRestauranteAndCategoriaAndDisponible(@Param("restauranteId") Long restauranteId, @Param("categoriaId") Long categoriaId);

    @Query("SELECT COUNT(m) FROM Menu m WHERE m.restaurante.id = :restauranteId")
    Long countByRestauranteId(@Param("restauranteId") Long restauranteId);

    @Query("SELECT COUNT(m) FROM Menu m WHERE m.restaurante.id = :restauranteId AND m.disponible = true")
    Long countByRestauranteIdAndDisponibleTrue(@Param("restauranteId") Long restauranteId);

    @Query("SELECT AVG(m.precio) FROM Menu m WHERE m.restaurante.id = :restauranteId AND m.disponible = true")
    Optional<BigDecimal> calcularPrecioPromedioPorRestaurante(@Param("restauranteId") Long restauranteId);

    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.categoria LEFT JOIN FETCH m.restaurante WHERE m.id = :menuId")
    Optional<Menu> findByIdWithDetails(@Param("menuId") Long menuId);

    @Query("SELECT m FROM Menu m WHERE m.precio = (SELECT MAX(menu.precio) FROM Menu menu WHERE menu.restaurante.id = :restauranteId)")
    List<Menu> findMenusMasCarosPorRestaurante(@Param("restauranteId") Long restauranteId);

    @Query("SELECT m.categoria.nombre, COUNT(m) FROM Menu m WHERE m.disponible = true GROUP BY m.categoria.nombre ORDER BY COUNT(m) DESC")
    List<Object[]> countMenusPorCategoria();

    boolean existsByRestauranteIdAndNombre(Long restauranteId, String nombre);
}
