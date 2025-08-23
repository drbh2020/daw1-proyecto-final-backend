package com.delivery.sistema.delivery.y.gestion.pedido.repository;

import com.delivery.sistema.delivery.y.gestion.pedido.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByPedidoId(Long pedidoId);

    List<DetallePedido> findByMenuId(Long menuId);

    @Query("SELECT dp FROM DetallePedido dp WHERE dp.pedido.id = :pedidoId ORDER BY dp.id ASC")
    List<DetallePedido> findDetallesByPedidoOrdenado(@Param("pedidoId") Long pedidoId);

    @Query("SELECT dp FROM DetallePedido dp WHERE dp.menu.restaurante.id = :restauranteId ORDER BY dp.pedido.fechaPedido DESC")
    List<DetallePedido> findDetallesByRestaurante(@Param("restauranteId") Long restauranteId);

    @Query("SELECT SUM(dp.cantidad) FROM DetallePedido dp WHERE dp.menu.id = :menuId")
    Long getTotalCantidadVendidaByMenu(@Param("menuId") Long menuId);

    @Query("SELECT dp.menu.id, dp.menu.nombre, SUM(dp.cantidad) as totalVendido FROM DetallePedido dp WHERE dp.menu.restaurante.id = :restauranteId GROUP BY dp.menu.id, dp.menu.nombre ORDER BY totalVendido DESC")
    List<Object[]> getMenusMasVendidosByRestaurante(@Param("restauranteId") Long restauranteId);

    @Query("SELECT COUNT(dp) FROM DetallePedido dp WHERE dp.pedido.id = :pedidoId")
    Long countDetallesByPedido(@Param("pedidoId") Long pedidoId);

    void deleteByPedidoId(Long pedidoId);
}