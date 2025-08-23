package com.delivery.sistema.delivery.y.gestion.shared.service;

import com.delivery.sistema.delivery.y.gestion.shared.model.Calificacion;
import com.delivery.sistema.delivery.y.gestion.pedido.model.Pedido;
import com.delivery.sistema.delivery.y.gestion.cliente.model.Cliente;
import com.delivery.sistema.delivery.y.gestion.restaurante.model.Restaurante;
import com.delivery.sistema.delivery.y.gestion.pedido.model.EstadoPedido;
import com.delivery.sistema.delivery.y.gestion.shared.repository.CalificacionRepository;
import com.delivery.sistema.delivery.y.gestion.pedido.repository.PedidoRepository;
import com.delivery.sistema.delivery.y.gestion.cliente.repository.ClienteRepository;
import com.delivery.sistema.delivery.y.gestion.restaurante.repository.RestauranteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;

    public List<Calificacion> listarTodas() {
        return calificacionRepository.findAll();
    }

    public List<Calificacion> listarPorRestaurante(Long restauranteId) {
        return calificacionRepository.findByRestauranteIdOrderByFechaCalificacionDesc(restauranteId);
    }

    public List<Calificacion> listarPorCliente(Long clienteId) {
        return calificacionRepository.findByClienteIdOrderByFechaCalificacionDesc(clienteId);
    }

    public List<Calificacion> listarPorPuntuacion(Integer puntuacion) {
        return calificacionRepository.findByPuntuacionOrderByFechaCalificacionDesc(puntuacion);
    }

    public List<Calificacion> listarRecientes() {
        return calificacionRepository.findTop10ByOrderByFechaCalificacionDesc();
    }

    public Page<Calificacion> listarPaginado(Pageable pageable) {
        return calificacionRepository.findAll(pageable);
    }

    public Page<Calificacion> listarPorRestaurantePaginado(Long restauranteId, Pageable pageable) {
        return calificacionRepository.findByRestauranteId(restauranteId, pageable);
    }

    public Optional<Calificacion> buscarPorId(Long id) {
        return calificacionRepository.findById(id);
    }

    public Calificacion obtenerPorId(Long id) {
        return calificacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Calificación no encontrada con ID: " + id));
    }

    public Optional<Calificacion> buscarPorPedido(Long pedidoId) {
        return calificacionRepository.findByPedidoId(pedidoId);
    }

    public Calificacion crear(Calificacion calificacion) {
        // Validar pedido
        Pedido pedido = pedidoRepository.findById(calificacion.getPedido().getId())
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado"));

        // Verificar que el pedido esté entregado
        if (pedido.getEstado() != EstadoPedido.ENTREGADO) {
            throw new IllegalStateException("Solo se pueden calificar pedidos entregados");
        }

        // Verificar que no exista ya una calificación para este pedido
        if (calificacionRepository.existsByPedidoId(pedido.getId())) {
            throw new IllegalArgumentException("Ya existe una calificación para este pedido");
        }

        // Validar cliente
        Cliente cliente = clienteRepository.findById(calificacion.getCliente().getId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        // Verificar que el cliente sea el dueño del pedido
        if (!pedido.getCliente().getId().equals(cliente.getId())) {
            throw new IllegalArgumentException("El cliente no puede calificar un pedido que no es suyo");
        }

        // Validar restaurante
        Restaurante restaurante = restauranteRepository.findById(calificacion.getRestaurante().getId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante no encontrado"));

        // Verificar que el restaurante sea el del pedido
        if (!pedido.getRestaurante().getId().equals(restaurante.getId())) {
            throw new IllegalArgumentException("El restaurante no corresponde al pedido");
        }

        // Validar puntuación
        if (calificacion.getPuntuacion() < 1 || calificacion.getPuntuacion() > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5");
        }

        // Configurar entidades completas
        calificacion.setPedido(pedido);
        calificacion.setCliente(cliente);
        calificacion.setRestaurante(restaurante);

        return calificacionRepository.save(calificacion);
    }

    public Calificacion actualizar(Long id, Calificacion calificacionActualizada) {
        Calificacion calificacionExistente = obtenerPorId(id);

        // Solo permitir actualizar puntuación y comentario
        // Validar puntuación
        if (calificacionActualizada.getPuntuacion() < 1 || calificacionActualizada.getPuntuacion() > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5");
        }

        calificacionExistente.setPuntuacion(calificacionActualizada.getPuntuacion());
        calificacionExistente.setComentario(calificacionActualizada.getComentario());

        return calificacionRepository.save(calificacionExistente);
    }

    public void eliminar(Long id) {
        Calificacion calificacion = obtenerPorId(id);
        calificacionRepository.delete(calificacion);
    }

    @Transactional(readOnly = true)
    public boolean existePorId(Long id) {
        return calificacionRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existePorPedido(Long pedidoId) {
        return calificacionRepository.existsByPedidoId(pedidoId);
    }

    @Transactional(readOnly = true)
    public Double calcularPromedioRestaurante(Long restauranteId) {
        return calificacionRepository.calcularPromedioByRestaurante(restauranteId)
                .orElse(0.0);
    }

    @Transactional(readOnly = true)
    public Long contarCalificacionesRestaurante(Long restauranteId) {
        return calificacionRepository.countByRestauranteId(restauranteId);
    }

    @Transactional(readOnly = true)
    public Long contarPorPuntuacionYRestaurante(Long restauranteId, Integer puntuacion) {
        return calificacionRepository.countByRestauranteIdAndPuntuacion(restauranteId, puntuacion);
    }

    @Transactional(readOnly = true)
    public List<Calificacion> obtenerMejoresCalificaciones(Long restauranteId, Integer limit) {
        return calificacionRepository.findTopCalificacionesByRestaurante(restauranteId, limit);
    }

    @Transactional(readOnly = true)
    public List<Calificacion> obtenerCalificacionesRecientesPorRestaurante(Long restauranteId, Integer limit) {
        return calificacionRepository.findRecentCalificacionesByRestaurante(restauranteId, limit);
    }

    @Transactional(readOnly = true)
    public Long contarTotal() {
        return calificacionRepository.count();
    }

    @Transactional(readOnly = true)
    public Long contarPorCliente(Long clienteId) {
        return calificacionRepository.countByClienteId(clienteId);
    }

    @Transactional(readOnly = true)
    public List<Calificacion> obtenerEstadisticasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return calificacionRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public Calificacion obtenerConDetalle(Long id) {
        return calificacionRepository.findByIdWithPedidoAndClienteAndRestaurante(id)
                .orElseThrow(() -> new EntityNotFoundException("Calificación no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public boolean puedeCalificar(Long clienteId, Long pedidoId) {
        // Verificar que el pedido esté entregado y que el cliente sea el dueño
        return pedidoRepository.existsByIdAndClienteIdAndEstado(pedidoId, clienteId, EstadoPedido.ENTREGADO) &&
               !calificacionRepository.existsByPedidoId(pedidoId);
    }
}