package com.delivery.sistema.delivery.y.gestion.delivery.service;

import com.delivery.sistema.delivery.y.gestion.delivery.model.Entrega;
import com.delivery.sistema.delivery.y.gestion.delivery.dto.EntregaDto;
import com.delivery.sistema.delivery.y.gestion.pedido.model.Pedido;
import com.delivery.sistema.delivery.y.gestion.delivery.model.Repartidor;
import com.delivery.sistema.delivery.y.gestion.delivery.model.EstadoEntrega;
import com.delivery.sistema.delivery.y.gestion.delivery.model.EstadoRepartidor;
import com.delivery.sistema.delivery.y.gestion.pedido.model.EstadoPedido;
import com.delivery.sistema.delivery.y.gestion.delivery.repository.EntregaRepository;
import com.delivery.sistema.delivery.y.gestion.pedido.repository.PedidoRepository;
import com.delivery.sistema.delivery.y.gestion.delivery.repository.RepartidorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final PedidoRepository pedidoRepository;
    private final RepartidorRepository repartidorRepository;

    public List<Entrega> listarTodas() {
        return entregaRepository.findAll();
    }

    public List<Entrega> listarPorEstado(EstadoEntrega estado) {
        return entregaRepository.findByEstadoEntregaOrderByFechaAsignacionAsc(estado);
    }

    public List<Entrega> listarPorRepartidor(Long repartidorId) {
        return entregaRepository.findByRepartidorIdOrderByFechaAsignacionDesc(repartidorId);
    }

    public List<Entrega> listarEntregasActivas() {
        return entregaRepository.findEntregasActivas();
    }

    public List<Entrega> listarEntregasPendientes() {
        return entregaRepository.findByEstadoEntregaOrderByFechaAsignacionAsc(EstadoEntrega.ASIGNADO);
    }

    public Page<Entrega> listarPaginado(Pageable pageable) {
        return entregaRepository.findAll(pageable);
    }

    public Page<Entrega> listarPorRepartidorPaginado(Long repartidorId, Pageable pageable) {
        return entregaRepository.findByRepartidorId(repartidorId, pageable);
    }

    public Optional<Entrega> buscarPorId(Long id) {
        return entregaRepository.findById(id);
    }

    public Entrega obtenerPorId(Long id) {
        return entregaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entrega no encontrada con ID: " + id));
    }

    public Optional<Entrega> buscarPorPedido(Long pedidoId) {
        return entregaRepository.findByPedidoId(pedidoId);
    }

    public Entrega crearEntrega(Long pedidoId, Long repartidorId) {
        // Validar pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con ID: " + pedidoId));

        // Validar que el pedido esté listo para entrega
        if (pedido.getEstado() != EstadoPedido.LISTO) {
            throw new IllegalStateException("Solo se pueden crear entregas para pedidos listos");
        }

        // Verificar que no tenga entrega asignada
        if (entregaRepository.existsByPedidoId(pedidoId)) {
            throw new IllegalStateException("El pedido ya tiene una entrega asignada");
        }

        // Validar repartidor
        Repartidor repartidor = repartidorRepository.findById(repartidorId)
                .orElseThrow(() -> new EntityNotFoundException("Repartidor no encontrado con ID: " + repartidorId));

        // Validar que el repartidor esté disponible
        if (repartidor.getEstado() != EstadoRepartidor.LIBRE) {
            throw new IllegalStateException("El repartidor no está disponible");
        }

        // Crear entrega
        Entrega entrega = new Entrega();
        entrega.setPedido(pedido);
        entrega.setRepartidor(repartidor);
        entrega.setEstadoEntrega(EstadoEntrega.ASIGNADO);

        Entrega entregaGuardada = entregaRepository.save(entrega);

        // Actualizar estado del pedido
        pedido.setEstado(EstadoPedido.EN_TRANSITO);
        pedidoRepository.save(pedido);

        return entregaGuardada;
    }

    public Entrega marcarEnCamino(Long id) {
        Entrega entrega = obtenerPorId(id);

        if (entrega.getEstadoEntrega() != EstadoEntrega.ASIGNADO) {
            throw new IllegalStateException("Solo se pueden marcar en camino entregas asignadas");
        }

        entrega.setEstadoEntrega(EstadoEntrega.EN_CAMINO);
        return entregaRepository.save(entrega);
    }

    public Entrega marcarEntregada(Long id, String comentarios) {
        Entrega entrega = obtenerPorId(id);

        if (entrega.getEstadoEntrega() != EstadoEntrega.EN_CAMINO) {
            throw new IllegalStateException("Solo se pueden entregar pedidos en camino");
        }

        entrega.setEstadoEntrega(EstadoEntrega.ENTREGADO);
        entrega.setComentarios(comentarios);

        Entrega entregaActualizada = entregaRepository.save(entrega);

        // Actualizar estado del pedido
        Pedido pedido = entrega.getPedido();
        pedido.setEstado(EstadoPedido.ENTREGADO);
        pedidoRepository.save(pedido);

        return entregaActualizada;
    }

    public Entrega marcarFallida(Long id, String motivoFallo) {
        Entrega entrega = obtenerPorId(id);

        if (entrega.getEstadoEntrega() == EstadoEntrega.ENTREGADO) {
            throw new IllegalStateException("No se puede marcar como fallida una entrega ya completada");
        }

        entrega.setEstadoEntrega(EstadoEntrega.FALLIDO);
        entrega.setComentarios(motivoFallo);

        Entrega entregaActualizada = entregaRepository.save(entrega);

        // Actualizar estado del pedido
        Pedido pedido = entrega.getPedido();
        pedido.setEstado(EstadoPedido.LISTO); // Volver a listo para reasignar
        pedidoRepository.save(pedido);

        return entregaActualizada;
    }

    public Entrega actualizarUbicacion(Long id, BigDecimal latitud, BigDecimal longitud) {
        Entrega entrega = obtenerPorId(id);

        if (entrega.getEstadoEntrega() != EstadoEntrega.EN_CAMINO) {
            throw new IllegalStateException("Solo se puede actualizar ubicación de entregas en camino");
        }

        entrega.setLatitud(latitud);
        entrega.setLongitud(longitud);

        return entregaRepository.save(entrega);
    }

    public Entrega actualizarComentarios(Long id, String comentarios) {
        Entrega entrega = obtenerPorId(id);
        entrega.setComentarios(comentarios);
        return entregaRepository.save(entrega);
    }

    public void eliminar(Long id) {
        Entrega entrega = obtenerPorId(id);

        // Solo permitir eliminar entregas fallidas o asignadas (no iniciadas)
        if (entrega.getEstadoEntrega() == EstadoEntrega.EN_CAMINO || 
            entrega.getEstadoEntrega() == EstadoEntrega.ENTREGADO) {
            throw new IllegalStateException("No se pueden eliminar entregas en progreso o completadas");
        }

        entregaRepository.delete(entrega);
    }

    public Entrega reasignarRepartidor(Long entregaId, Long nuevoRepartidorId) {
        Entrega entrega = obtenerPorId(entregaId);

        // Solo permitir reasignar entregas asignadas o fallidas
        if (entrega.getEstadoEntrega() != EstadoEntrega.ASIGNADO && 
            entrega.getEstadoEntrega() != EstadoEntrega.FALLIDO) {
            throw new IllegalStateException("Solo se pueden reasignar entregas asignadas o fallidas");
        }

        // Validar nuevo repartidor
        Repartidor nuevoRepartidor = repartidorRepository.findById(nuevoRepartidorId)
                .orElseThrow(() -> new EntityNotFoundException("Repartidor no encontrado con ID: " + nuevoRepartidorId));

        if (nuevoRepartidor.getEstado() != EstadoRepartidor.LIBRE) {
            throw new IllegalStateException("El nuevo repartidor no está disponible");
        }

        entrega.setRepartidor(nuevoRepartidor);
        entrega.setEstadoEntrega(EstadoEntrega.ASIGNADO);
        entrega.setComentarios(null); // Limpiar comentarios anteriores

        return entregaRepository.save(entrega);
    }

    @Transactional(readOnly = true)
    public boolean existePorId(Long id) {
        return entregaRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existePorPedido(Long pedidoId) {
        return entregaRepository.existsByPedidoId(pedidoId);
    }

    @Transactional(readOnly = true)
    public Long contarTotal() {
        return entregaRepository.count();
    }

    @Transactional(readOnly = true)
    public Long contarPorEstado(EstadoEntrega estado) {
        return entregaRepository.countByEstadoEntrega(estado);
    }

    @Transactional(readOnly = true)
    public Long contarPorRepartidor(Long repartidorId) {
        return entregaRepository.countByRepartidorId(repartidorId);
    }

    @Transactional(readOnly = true)
    public Long contarEntregasCompletadasPorRepartidor(Long repartidorId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return entregaRepository.countEntregasCompletadasByRepartidorAndFechas(repartidorId, fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public List<Entrega> obtenerHistorialPorRepartidor(Long repartidorId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return entregaRepository.findByRepartidorIdAndFechaEntregaBetween(repartidorId, fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public Entrega obtenerConDetalle(Long id) {
        return entregaRepository.findByIdWithPedidoAndRepartidor(id)
                .orElseThrow(() -> new EntityNotFoundException("Entrega no encontrada con ID: " + id));
    }

    // Métodos de conversión
    private EntregaDto convertirADto(Entrega entrega) {
        EntregaDto dto = new EntregaDto();
        dto.setId(entrega.getId());
        dto.setPedidoId(entrega.getPedido().getId());
        dto.setRepartidorId(entrega.getRepartidor().getId());
        dto.setRepartidorNombre(entrega.getRepartidor().getCliente().getNombre());
        dto.setEstadoEntrega(entrega.getEstadoEntrega());
        dto.setFechaInicio(entrega.getFechaAsignacion());
        dto.setFechaEntrega(entrega.getFechaEntrega());
        dto.setComentarios(entrega.getComentarios());
        dto.setLatitud(entrega.getLatitud());
        dto.setLongitud(entrega.getLongitud());
        return dto;
    }

    // Métodos adicionales requeridos por el controller
    public Page<EntregaDto> listarEntregasPorEstado(EstadoEntrega estado, Pageable pageable) {
        return entregaRepository.findByEstadoEntrega(estado, pageable).map(this::convertirADto);
    }

    public Page<EntregaDto> listarEntregasPorRepartidor(Long repartidorId, Pageable pageable) {
        return listarPorRepartidorPaginado(repartidorId, pageable).map(this::convertirADto);
    }

    public Page<EntregaDto> listarEntregas(Pageable pageable) {
        return listarPaginado(pageable).map(this::convertirADto);
    }

    public Page<EntregaDto> listarEntregasActivas(Pageable pageable) {
        return entregaRepository.findEntregasActivas(pageable).map(this::convertirADto);
    }

    public Page<EntregaDto> listarEntregasPorRepartidorYEstado(Long repartidorId, EstadoEntrega estado, Pageable pageable) {
        return entregaRepository.findByRepartidorIdAndEstadoEntrega(repartidorId, estado, pageable).map(this::convertirADto);
    }

    public EntregaDto obtenerEntregaPorId(Long id) {
        return convertirADto(obtenerPorId(id));
    }

    public EntregaDto crearEntrega(EntregaDto entregaDto) {
        Entrega entrega = crearEntrega(entregaDto.getPedidoId(), entregaDto.getRepartidorId());
        return convertirADto(entrega);
    }

    public EntregaDto actualizarEntrega(Long id, EntregaDto entregaDto) {
        Entrega entrega = obtenerPorId(id);
        
        // Solo actualizar comentarios si la entrega está en progreso
        if (entregaDto.getComentarios() != null) {
            entrega.setComentarios(entregaDto.getComentarios());
        }
        
        Entrega resultado = entregaRepository.save(entrega);
        return convertirADto(resultado);
    }

    public void eliminarEntrega(Long id) {
        eliminar(id);
    }

    // Métodos adicionales para el controller
    public EntregaDto asignarRepartidor(Long entregaId, Long repartidorId) {
        Entrega entrega = reasignarRepartidor(entregaId, repartidorId);
        return convertirADto(entrega);
    }

    public EntregaDto cambiarEstado(Long id, EstadoEntrega estado) {
        Entrega entrega = obtenerPorId(id);
        
        // Validar transiciones de estado
        if (estado == EstadoEntrega.EN_CAMINO) {
            entrega = marcarEnCamino(id);
        } else if (estado == EstadoEntrega.ENTREGADO) {
            entrega = marcarEntregada(id, null);
        } else if (estado == EstadoEntrega.FALLIDO) {
            entrega = marcarFallida(id, "Estado cambiado por administrador");
        }
        
        return convertirADto(entrega);
    }

    public EntregaDto iniciarEntrega(Long id) {
        Entrega entrega = marcarEnCamino(id);
        return convertirADto(entrega);
    }

    public EntregaDto finalizarEntrega(Long id, String comentarios) {
        Entrega entrega = marcarEntregada(id, comentarios);
        return convertirADto(entrega);
    }

    public Map<String, Object> obtenerEstadisticasEntregas() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        estadisticas.put("totalEntregas", entregaRepository.count());
        estadisticas.put("entregasAsignadas", entregaRepository.countByEstadoEntrega(EstadoEntrega.ASIGNADO));
        estadisticas.put("entregasEnCamino", entregaRepository.countByEstadoEntrega(EstadoEntrega.EN_CAMINO));
        estadisticas.put("entregasCompletadas", entregaRepository.countByEstadoEntrega(EstadoEntrega.ENTREGADO));
        estadisticas.put("entregasFallidas", entregaRepository.countByEstadoEntrega(EstadoEntrega.FALLIDO));
        
        return estadisticas;
    }

    public void cancelarEntrega(Long id) {
        marcarFallida(id, "Entrega cancelada");
    }
}