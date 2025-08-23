package com.delivery.sistema.delivery.y.gestion.restaurante.repository;

import com.delivery.sistema.delivery.y.gestion.restaurante.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByActivoTrueOrderByOrdenMostrarAsc();
    
    Page<Categoria> findByActivoTrueOrderByOrdenMostrarAsc(Pageable pageable);
    
    Page<Categoria> findAllByOrderByOrdenMostrarAsc(Pageable pageable);

    List<Categoria> findByActivoOrderByOrdenMostrarAsc(Boolean activo);

    Optional<Categoria> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    @Query("SELECT c FROM Categoria c WHERE c.activo = true AND c.menus IS NOT EMPTY ORDER BY c.ordenMostrar ASC")
    List<Categoria> findCategoriasConMenus();

    @Query("SELECT c FROM Categoria c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND c.activo = true ORDER BY c.ordenMostrar ASC")
    List<Categoria> findByNombreContainingIgnoreCaseAndActivoTrue(@Param("nombre") String nombre);

    @Query("SELECT COUNT(c.menus) FROM Categoria c WHERE c.id = :categoriaId")
    Long countMenusByCategoria(@Param("categoriaId") Long categoriaId);

    @Query("SELECT c FROM Categoria c LEFT JOIN FETCH c.menus WHERE c.id = :categoriaId")
    Optional<Categoria> findByIdWithMenus(@Param("categoriaId") Long categoriaId);

    @Query("SELECT c FROM Categoria c WHERE c.ordenMostrar = (SELECT MAX(cat.ordenMostrar) FROM Categoria cat)")
    Optional<Categoria> findCategoriaConMayorOrden();
}