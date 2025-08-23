package com.delivery.sistema.delivery.y.gestion.pedido.service;

import com.delivery.sistema.delivery.y.gestion.pedido.model.*;
import com.delivery.sistema.delivery.y.gestion.pedido.repository.*;
import com.delivery.sistema.delivery.y.gestion.cliente.model.Cliente;
import com.delivery.sistema.delivery.y.gestion.cliente.repository.ClienteRepository;
import com.delivery.sistema.delivery.y.gestion.restaurante.model.Restaurante;
import com.delivery.sistema.delivery.y.gestion.restaurante.model.Menu;
import com.delivery.sistema.delivery.y.gestion.restaurante.repository.RestauranteRepository;
import com.delivery.sistema.delivery.y.gestion.restaurante.repository.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.delivery.sistema.delivery.y.gestion.pedido.dto.PedidoDto;
import com.delivery.sistema.delivery.y.gestion.pedido.dto.CrearPedidoDto;
import com.delivery.sistema.delivery.y.gestion.pedido.dto.DetallePedidoDto;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final MenuRepository menuRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByFechaPedidoDesc(clienteId);
    }

    public List<Pedido> listarPorRestaurante(Long restauranteId) {
        return pedidoRepository.findByRestauranteIdOrderByFechaPedidoDesc(restauranteId);
    }

    public List<Pedido> listarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstadoOrderByFechaPedidoAsc(estado);
    }

    public List<Pedido> listarActivos() {
        return pedidoRepository.findPedidosActivos();
    }

    public Page<Pedido> listarPorClientePaginado(Long clienteId, Pageable pageable) {
        return pedidoRepository.findByClienteId(clienteId, pageable);
    }

    public Page<Pedido> listarPorRestaurantePaginado(Long restauranteId, Pageable pageable) {
        return pedidoRepository.findByRestauranteId(restauranteId, pageable);
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido obtenerPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con ID: " + id));
    }

    public Pedido crearPedido(Pedido pedido, List<DetallePedido> detalles) {
        // Validar cliente
        Cliente cliente = clienteRepository.findById(pedido.getCliente().getId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        // Validar restaurante
        Restaurante restaurante = restauranteRepository.findById(pedido.getRestaurante().getId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante no encontrado"));

        // Validar que el restaurante esté activo
        if (!restaurante.getActivo()) {
            throw new IllegalStateException("El restaurante no está disponible");
        }

        // Configurar pedido
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setEstado(EstadoPedido.PENDIENTE);

        // Calcular total
        BigDecimal total = calcularTotalPedido(detalles);
        total = total.add(pedido.getCostoDelivery());
        pedido.setTotal(total);

        // Guardar pedido
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Guardar detalles
        for (DetallePedido detalle : detalles) {
            // Validar menú
            Menu menu = menuRepository.findById(detalle.getMenu().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Menú no encontrado"));

            if (!menu.getDisponible()) {
                throw new IllegalStateException("El menú '" + menu.getNombre() + "' no está disponible");
            }

            // Configurar detalle
            detalle.setPedido(pedidoGuardado);
            detalle.setMenu(menu);
            detalle.setPrecioUnitario(menu.getPrecio());
            
            BigDecimal subtotal = menu.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            detalle.setSubtotal(subtotal);

            detallePedidoRepository.save(detalle);
        }

        return pedidoGuardado;
    }

    private BigDecimal calcularTotalPedido(List<DetallePedido> detalles) {
        return detalles.stream()
                .map(detalle -> {
                    Menu menu = menuRepository.findById(detalle.getMenu().getId())
                            .orElseThrow(() -> new EntityNotFoundException("Menú no encontrado"));
                    return menu.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Pedido confirmarPedido(Long id) {
        Pedido pedido = obtenerPorId(id);
        
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar pedidos pendientes");
        }
        
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return pedidoRepository.save(pedido);
    }

    public Pedido iniciarPreparacion(Long id) {
        Pedido pedido = obtenerPorId(id);
        
        if (pedido.getEstado() != EstadoPedido.CONFIRMADO) {
            throw new IllegalStateException("Solo se pueden preparar pedidos confirmados");
        }
        
        pedido.setEstado(EstadoPedido.EN_PREPARACION);
        return pedidoRepository.save(pedido);
    }

    public Pedido marcarListo(Long id) {
        Pedido pedido = obtenerPorId(id);
        
        if (pedido.getEstado() != EstadoPedido.EN_PREPARACION) {
            throw new IllegalStateException("Solo se pueden marcar como listos pedidos en preparación");
        }
        
        pedido.setEstado(EstadoPedido.LISTO);
        return pedidoRepository.save(pedido);
    }

    public Pedido marcarEnTransito(Long id) {
        Pedido pedido = obtenerPorId(id);
        
        if (pedido.getEstado() != EstadoPedido.LISTO) {
            throw new IllegalStateException("Solo se pueden poner en tránsito pedidos listos");
        }
        
        pedido.setEstado(EstadoPedido.EN_TRANSITO);
        return pedidoRepository.save(pedido);
    }

    public Pedido marcarEntregado(Long id) {
        Pedido pedido = obtenerPorId(id);
        
        if (pedido.getEstado() != EstadoPedido.EN_TRANSITO) {
            throw new IllegalStateException("Solo se pueden entregar pedidos en tránsito");
        }
        
        pedido.setEstado(EstadoPedido.ENTREGADO);
        return pedidoRepository.save(pedido);
    }

    public Pedido cancelarPedido(Long id) {
        Pedido pedido = obtenerPorId(id);
        
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new IllegalStateException("No se puede cancelar un pedido entregado");
        }
        
        pedido.setEstado(EstadoPedido.CANCELADO);
        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public Long contarPorRestauranteYEstado(Long restauranteId, EstadoPedido estado) {
        return pedidoRepository.countPedidosByRestauranteAndEstado(restauranteId, estado);
    }

    @Transactional(readOnly = true)
    public Double calcularVentasPorRestaurante(Long restauranteId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pedidoRepository.calcularVentasByRestauranteAndFechas(restauranteId, fechaInicio, fechaFin)
                .orElse(0.0);
    }

    @Transactional(readOnly = true)
    public List<DetallePedido> obtenerDetallesPedido(Long pedidoId) {
        return detallePedidoRepository.findByPedidoId(pedidoId);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> contarPedidosPorEstado() {
        Map<String, Long> estadisticas = new HashMap<>();
        
        for (EstadoPedido estado : EstadoPedido.values()) {
            estadisticas.put(estado.name(), pedidoRepository.countPedidosByEstado(estado));
        }
        
        return estadisticas;
    }

    // Métodos adicionales requeridos por PedidoController
    @Transactional(readOnly = true)
    public Page<PedidoDto> listarPedidosPorRestauranteYEstado(Long restauranteId, EstadoPedido estado, Pageable pageable) {
        return pedidoRepository.findByRestauranteIdAndEstado(restauranteId, estado, pageable)
                .map(this::convertirADto);
    }

    @Transactional(readOnly = true)
    public Page<PedidoDto> listarPedidosPorRestaurante(Long restauranteId, Pageable pageable) {
        return pedidoRepository.findByRestauranteId(restauranteId, pageable)
                .map(this::convertirADto);
    }

    public PedidoDto obtenerPedidoPorId(Long id) {
        return convertirADto(obtenerPorId(id));
    }

    public PedidoDto crearPedido(CrearPedidoDto crearPedidoDto) {
        // Convertir CrearPedidoDto a Pedido y DetallePedido
        Pedido pedido = convertirCrearDtoAEntidad(crearPedidoDto);
        List<DetallePedido> detalles = convertirDetallesDtoAEntidad(crearPedidoDto.getDetalles(), pedido);
        
        return convertirADto(crear(pedido, detalles));
    }

    public PedidoDto cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        return convertirADto(actualizarEstado(id, nuevoEstado));
    }

    public PedidoDto actualizarPedido(Long id, PedidoDto pedidoDto) {
        Pedido pedido = convertirDtoAEntidad(pedidoDto);
        return convertirADto(actualizar(id, pedido));
    }

    // Métodos adicionales requeridos por PedidoController
    @Transactional(readOnly = true)
    public Page<PedidoDto> listarPedidosPorEstado(EstadoPedido estado, Pageable pageable) {
        return pedidoRepository.findByEstado(estado, pageable).map(this::convertirADto);
    }

    @Transactional(readOnly = true)
    public Page<PedidoDto> listarPedidosPorCliente(Long clienteId, Pageable pageable) {
        return pedidoRepository.findByClienteId(clienteId, pageable).map(this::convertirADto);
    }

    @Transactional(readOnly = true)
    public Page<PedidoDto> listarPedidos(Pageable pageable) {
        return pedidoRepository.findAll(pageable).map(this::convertirADto);
    }

    // Métodos existentes renombrados para evitar conflictos
    public Pedido crear(Pedido pedido, List<DetallePedido> detalles) {
        return crearPedido(pedido, detalles);
    }

    public Pedido actualizarEstado(Long id, EstadoPedido nuevoEstado) {
        // Método genérico para cambiar estado
        Pedido pedido = obtenerPorId(id);
        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    public Pedido actualizar(Long id, Pedido pedidoActualizado) {
        Pedido pedidoExistente = obtenerPorId(id);
        // Actualizar campos básicos sin cambiar estado
        pedidoExistente.setNotas(pedidoActualizado.getNotas());
        pedidoExistente.setDireccionEntrega(pedidoActualizado.getDireccionEntrega());
        return pedidoRepository.save(pedidoExistente);
    }

    // Métodos de conversión DTO
    private PedidoDto convertirADto(Pedido pedido) {
        PedidoDto dto = new PedidoDto();
        dto.setId(pedido.getId());
        dto.setClienteId(pedido.getCliente().getId());
        dto.setRestauranteId(pedido.getRestaurante().getId());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setTotal(pedido.getTotal());
        dto.setEstado(pedido.getEstado());
        dto.setDireccionEntrega(pedido.getDireccionEntrega());
        dto.setNotas(pedido.getNotas());
        dto.setTiempoEstimado(pedido.getTiempoEstimado());
        dto.setCostoDelivery(pedido.getCostoDelivery());
        dto.setMetodoPago(pedido.getMetodoPago());
        return dto;
    }

    private Pedido convertirCrearDtoAEntidad(CrearPedidoDto dto) {
        Pedido pedido = new Pedido();
        // Configurar cliente y restaurante desde los IDs
        // Esta implementación se completará según las necesidades específicas
        return pedido;
    }

    private List<DetallePedido> convertirDetallesDtoAEntidad(List<DetallePedidoDto> detallesDto, Pedido pedido) {
        // Convertir detalles DTO a entidades
        // Esta implementación se completará según las necesidades específicas
        return new ArrayList<>();
    }

    private Pedido convertirDtoAEntidad(PedidoDto dto) {
        Pedido pedido = new Pedido();
        // Configurar todos los campos del pedido
        // Esta implementación se completará según las necesidades específicas
        return pedido;
    }
}