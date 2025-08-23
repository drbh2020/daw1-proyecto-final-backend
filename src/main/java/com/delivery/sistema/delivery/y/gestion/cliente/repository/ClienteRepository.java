package com.delivery.sistema.delivery.y.gestion.cliente.repository;

import com.delivery.sistema.delivery.y.gestion.cliente.model.Cliente;
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
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    Page<Cliente> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE c.fechaRegistro BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaRegistro DESC")
    List<Cliente> findByFechaRegistroBetween(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.direccion) LIKE LOWER(CONCAT('%', :direccion, '%'))")
    List<Cliente> findByDireccionContainingIgnoreCase(@Param("direccion") String direccion);

    @Query("SELECT c FROM Cliente c JOIN c.roles r WHERE r.nombre = :rolNombre")
    List<Cliente> findByRolNombre(@Param("rolNombre") String rolNombre);

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.fechaRegistro >= :fecha")
    Long countClientesRegistradosDespues(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.roles WHERE c.id = :clienteId")
    Optional<Cliente> findByIdWithRoles(@Param("clienteId") Long clienteId);

    @Query("SELECT c FROM Cliente c WHERE SIZE(c.roles) > 1")
    List<Cliente> findClientesConMultiplesRoles();

    // Métodos agregados para corregir discrepancias con ClienteService
    
    List<Cliente> findByEmailContainingIgnoreCase(String email);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.cliente.id = :clienteId")
    Long countPedidosByCliente(@Param("clienteId") Long clienteId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c JOIN c.roles r WHERE c.id = :clienteId AND r.nombre = :nombreRol")
    boolean hasRole(@Param("clienteId") Long clienteId, @Param("nombreRol") String nombreRol);

    List<Cliente> findByRolesNombre(String nombreRol);

    @Query("SELECT COUNT(c) FROM Cliente c JOIN c.roles r WHERE r.nombre = :nombreRol")
    Long countByRolesNombre(@Param("nombreRol") String nombreRol);

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.fechaRegistro BETWEEN :fechaInicio AND :fechaFin")
    Long countClientesNuevosByFechas(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
}
