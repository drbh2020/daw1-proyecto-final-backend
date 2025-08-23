package com.delivery.sistema.delivery.y.gestion.cliente.service;

import com.delivery.sistema.delivery.y.gestion.cliente.model.Cliente;
import com.delivery.sistema.delivery.y.gestion.cliente.dto.ClienteDto;
import com.delivery.sistema.delivery.y.gestion.shared.model.Rol;
import com.delivery.sistema.delivery.y.gestion.cliente.repository.ClienteRepository;
import com.delivery.sistema.delivery.y.gestion.shared.repository.RolRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Page<Cliente> listarPaginado(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    public List<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmailContainingIgnoreCase(email);
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + id));
    }

    public Optional<Cliente> buscarPorEmailExacto(String email) {
        return clienteRepository.findByEmail(email);
    }

    public Cliente crear(Cliente cliente) {
        // Validar que el email no esté duplicado
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("Ya existe un cliente con el email: " + cliente.getEmail());
        }

        // Encriptar password
        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));

        // Asignar rol CLIENTE por defecto si no tiene roles
        if (cliente.getRoles() == null || cliente.getRoles().isEmpty()) {
            Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                    .orElseThrow(() -> new EntityNotFoundException("Rol CLIENTE no encontrado"));
            cliente.setRoles(Set.of(rolCliente));
        }

        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente clienteActualizado) {
        Cliente clienteExistente = obtenerPorId(id);

        // Validar email único (excepto el propio)
        if (!clienteExistente.getEmail().equals(clienteActualizado.getEmail()) &&
            clienteRepository.existsByEmail(clienteActualizado.getEmail())) {
            throw new IllegalArgumentException("Ya existe un cliente con el email: " + clienteActualizado.getEmail());
        }

        // Actualizar campos (excepto password y roles)
        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setEmail(clienteActualizado.getEmail());
        clienteExistente.setDireccion(clienteActualizado.getDireccion());

        return clienteRepository.save(clienteExistente);
    }

    public void actualizarPassword(Long id, String passwordActual, String passwordNueva) {
        Cliente cliente = obtenerPorId(id);

        // Verificar password actual
        if (!passwordEncoder.matches(passwordActual, cliente.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        cliente.setPassword(passwordEncoder.encode(passwordNueva));
        clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        Cliente cliente = obtenerPorId(id);
        
        // Verificar que no tenga pedidos asociados
        Long cantidadPedidos = clienteRepository.countPedidosByCliente(id);
        if (cantidadPedidos > 0) {
            throw new IllegalStateException("No se puede eliminar el cliente porque tiene " + cantidadPedidos + " pedidos asociados");
        }

        clienteRepository.delete(cliente);
    }

    public Cliente asignarRol(Long clienteId, String nombreRol) {
        Cliente cliente = obtenerPorId(clienteId);
        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado: " + nombreRol));

        cliente.getRoles().add(rol);
        return clienteRepository.save(cliente);
    }

    public Cliente removerRol(Long clienteId, String nombreRol) {
        Cliente cliente = obtenerPorId(clienteId);
        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado: " + nombreRol));

        // No permitir remover el último rol
        if (cliente.getRoles().size() <= 1) {
            throw new IllegalStateException("No se puede remover el último rol del cliente");
        }

        cliente.getRoles().remove(rol);
        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public boolean existePorId(Long id) {
        return clienteRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean tieneRol(Long clienteId, String nombreRol) {
        return clienteRepository.hasRole(clienteId, nombreRol);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarPorRol(String nombreRol) {
        return clienteRepository.findByRolesNombre(nombreRol);
    }

    @Transactional(readOnly = true)
    public Long contarTotal() {
        return clienteRepository.count();
    }

    @Transactional(readOnly = true)
    public Long contarPorRol(String nombreRol) {
        return clienteRepository.countByRolesNombre(nombreRol);
    }

    @Transactional(readOnly = true)
    public Cliente obtenerConRoles(Long id) {
        return clienteRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Map<String, Long> obtenerEstadisticasBasicas() {
        Map<String, Long> estadisticas = new HashMap<>();
        
        estadisticas.put("totalClientes", clienteRepository.count());
        estadisticas.put("clientesActivos", clienteRepository.countByRolesNombre("CLIENTE"));
        estadisticas.put("restaurantes", clienteRepository.countByRolesNombre("RESTAURANTE"));
        estadisticas.put("repartidores", clienteRepository.countByRolesNombre("REPARTIDOR"));
        
        return estadisticas;
    }

    // Métodos de conversión
    private ClienteDto convertirADto(Cliente cliente) {
        ClienteDto dto = new ClienteDto();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setEmail(cliente.getEmail());
        dto.setDireccion(cliente.getDireccion());
        dto.setFechaRegistro(cliente.getFechaRegistro());
        dto.setRoles(cliente.getRoles().stream()
                .map(Rol::getNombre)
                .collect(Collectors.toSet()));
        return dto;
    }

    // Métodos adicionales requeridos por el controller
    public Page<ClienteDto> listarClientes(Pageable pageable) {
        return listarPaginado(pageable).map(this::convertirADto);
    }

    public ClienteDto obtenerClientePorId(Long id) {
        return convertirADto(obtenerPorId(id));
    }

    public ClienteDto actualizarCliente(Long id, ClienteDto clienteDto) {
        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setNombre(clienteDto.getNombre());
        clienteActualizado.setEmail(clienteDto.getEmail());
        clienteActualizado.setDireccion(clienteDto.getDireccion());
        Cliente resultado = actualizar(id, clienteActualizado);
        return convertirADto(resultado);
    }

    public void eliminarCliente(Long id) {
        eliminar(id);
    }
}