package com.delivery.sistema.delivery.y.gestion.delivery.service;

import com.delivery.sistema.delivery.y.gestion.delivery.model.Repartidor;
import com.delivery.sistema.delivery.y.gestion.cliente.model.Cliente;
import com.delivery.sistema.delivery.y.gestion.delivery.model.EstadoRepartidor;
import com.delivery.sistema.delivery.y.gestion.delivery.repository.RepartidorRepository;
import com.delivery.sistema.delivery.y.gestion.cliente.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RepartidorService {

    private final RepartidorRepository repartidorRepository;
    private final ClienteRepository clienteRepository;

    public List<Repartidor> listarTodos() {
        return repartidorRepository.findAll();
    }

    public List<Repartidor> listarLibres() {
        return repartidorRepository.findByEstadoOrderByFechaRegistroAsc(EstadoRepartidor.LIBRE);
    }

    public List<Repartidor> listarOcupados() {
        return repartidorRepository.findByEstadoOrderByFechaRegistroAsc(EstadoRepartidor.OCUPADO);
    }

    public List<Repartidor> listarActivos() {
        return repartidorRepository.findRepartidoresActivos();
    }

    public List<Repartidor> listarDisponibles() {
        return repartidorRepository.findByDisponibleTrueAndEstadoOrderByFechaRegistroAsc(EstadoRepartidor.LIBRE);
    }

    public Page<Repartidor> listarPaginado(Pageable pageable) {
        return repartidorRepository.findAll(pageable);
    }

    public Page<Repartidor> listarDisponiblesPaginado(Pageable pageable) {
        return repartidorRepository.findByDisponibleTrue(pageable);
    }

    public Optional<Repartidor> buscarPorId(Long id) {
        return repartidorRepository.findById(id);
    }

    public Repartidor obtenerPorId(Long id) {
        return repartidorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Repartidor no encontrado con ID: " + id));
    }

    public Optional<Repartidor> buscarPorCliente(Long clienteId) {
        return repartidorRepository.findByClienteId(clienteId);
    }

    public List<Repartidor> buscarPorVehiculo(String vehiculo) {
        return repartidorRepository.findByVehiculoContainingIgnoreCase(vehiculo);
    }

    public Repartidor crear(Repartidor repartidor) {
        // Validar que el cliente existe
        Cliente cliente = clienteRepository.findById(repartidor.getCliente().getId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + repartidor.getCliente().getId()));

        // Validar que el cliente no sea ya repartidor
        if (repartidorRepository.existsByClienteId(cliente.getId())) {
            throw new IllegalArgumentException("El cliente ya está registrado como repartidor");
        }

        repartidor.setCliente(cliente);
        
        // Configurar estado inicial si no está definido
        if (repartidor.getEstado() == null) {
            repartidor.setEstado(EstadoRepartidor.LIBRE);
        }

        return repartidorRepository.save(repartidor);
    }

    public Repartidor actualizar(Long id, Repartidor repartidorActualizado) {
        Repartidor repartidorExistente = obtenerPorId(id);

        // Actualizar campos
        repartidorExistente.setTelefono(repartidorActualizado.getTelefono());
        repartidorExistente.setVehiculo(repartidorActualizado.getVehiculo());
        repartidorExistente.setDisponible(repartidorActualizado.getDisponible());

        return repartidorRepository.save(repartidorExistente);
    }

    public void eliminar(Long id) {
        Repartidor repartidor = obtenerPorId(id);
        
        // Verificar que no tenga entregas asignadas activas
        Long entregasActivas = repartidorRepository.countEntregasActivasByRepartidor(id);
        if (entregasActivas > 0) {
            throw new IllegalStateException("No se puede eliminar el repartidor porque tiene " + entregasActivas + " entregas activas");
        }

        repartidorRepository.delete(repartidor);
    }

    public Repartidor cambiarEstado(Long id, EstadoRepartidor nuevoEstado) {
        Repartidor repartidor = obtenerPorId(id);
        
        // Validaciones de cambio de estado
        if (nuevoEstado == EstadoRepartidor.OCUPADO && !repartidor.getDisponible()) {
            throw new IllegalStateException("No se puede ocupar un repartidor no disponible");
        }

        repartidor.setEstado(nuevoEstado);
        return repartidorRepository.save(repartidor);
    }

    public Repartidor marcarLibre(Long id) {
        return cambiarEstado(id, EstadoRepartidor.LIBRE);
    }

    public Repartidor marcarOcupado(Long id) {
        return cambiarEstado(id, EstadoRepartidor.OCUPADO);
    }

    public Repartidor marcarInactivo(Long id) {
        return cambiarEstado(id, EstadoRepartidor.INACTIVO);
    }

    public Repartidor activar(Long id) {
        Repartidor repartidor = obtenerPorId(id);
        repartidor.setDisponible(true);
        repartidor.setEstado(EstadoRepartidor.LIBRE);
        return repartidorRepository.save(repartidor);
    }

    public Repartidor desactivar(Long id) {
        Repartidor repartidor = obtenerPorId(id);
        repartidor.setDisponible(false);
        repartidor.setEstado(EstadoRepartidor.INACTIVO);
        return repartidorRepository.save(repartidor);
    }

    public Optional<Repartidor> asignarRepartidorLibre() {
        List<Repartidor> libres = listarDisponibles();
        if (libres.isEmpty()) {
            return Optional.empty();
        }
        
        // Tomar el primer repartidor disponible
        Repartidor repartidor = libres.get(0);
        repartidor.setEstado(EstadoRepartidor.OCUPADO);
        repartidorRepository.save(repartidor);
        
        return Optional.of(repartidor);
    }

    @Transactional(readOnly = true)
    public boolean existePorId(Long id) {
        return repartidorRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existePorCliente(Long clienteId) {
        return repartidorRepository.existsByClienteId(clienteId);
    }

    @Transactional(readOnly = true)
    public Long contarTotal() {
        return repartidorRepository.count();
    }

    @Transactional(readOnly = true)
    public Long contarDisponibles() {
        return repartidorRepository.countByDisponibleTrue();
    }

    @Transactional(readOnly = true)
    public Long contarPorEstado(EstadoRepartidor estado) {
        return repartidorRepository.countByEstado(estado);
    }

    @Transactional(readOnly = true)
    public Long contarEntregasCompletadas(Long repartidorId) {
        return repartidorRepository.countEntregasCompletadasByRepartidor(repartidorId);
    }

    @Transactional(readOnly = true)
    public Repartidor obtenerConEntregas(Long id) {
        return repartidorRepository.findByIdWithEntregas(id)
                .orElseThrow(() -> new EntityNotFoundException("Repartidor no encontrado con ID: " + id));
    }
}