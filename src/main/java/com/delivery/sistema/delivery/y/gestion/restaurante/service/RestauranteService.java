package com.delivery.sistema.delivery.y.gestion.restaurante.service;

import com.delivery.sistema.delivery.y.gestion.restaurante.model.Restaurante;
import com.delivery.sistema.delivery.y.gestion.restaurante.dto.RestauranteDto;
import com.delivery.sistema.delivery.y.gestion.cliente.model.Cliente;
import com.delivery.sistema.delivery.y.gestion.restaurante.repository.RestauranteRepository;
import com.delivery.sistema.delivery.y.gestion.cliente.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final ClienteRepository clienteRepository;

    public List<Restaurante> listarTodos() {
        return restauranteRepository.findAll();
    }

    public List<Restaurante> listarActivos() {
        return restauranteRepository.findByActivoTrueOrderByNombreAsc();
    }

    public Page<Restaurante> listarPaginado(Pageable pageable) {
        return restauranteRepository.findAll(pageable);
    }

    public Page<Restaurante> listarActivosPaginado(Pageable pageable) {
        return restauranteRepository.findByActivoTrue(pageable);
    }

    public Optional<Restaurante> buscarPorId(Long id) {
        return restauranteRepository.findById(id);
    }

    public Restaurante obtenerPorId(Long id) {
        return restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante no encontrado con ID: " + id));
    }

    public List<Restaurante> buscarPorNombre(String nombre) {
        return restauranteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Restaurante> buscarPorCliente(Long clienteId) {
        return restauranteRepository.findByClienteId(clienteId);
    }

    public Restaurante crear(Restaurante restaurante) {
        // Validar que el cliente existe
        Cliente cliente = clienteRepository.findById(restaurante.getCliente().getId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + restaurante.getCliente().getId()));
        
        restaurante.setCliente(cliente);
        return restauranteRepository.save(restaurante);
    }

    public Restaurante actualizar(Long id, Restaurante restauranteActualizado) {
        Restaurante restauranteExistente = obtenerPorId(id);
        
        // Actualizar campos
        restauranteExistente.setNombre(restauranteActualizado.getNombre());
        restauranteExistente.setDescripcion(restauranteActualizado.getDescripcion());
        restauranteExistente.setDireccion(restauranteActualizado.getDireccion());
        restauranteExistente.setTelefono(restauranteActualizado.getTelefono());
        restauranteExistente.setHoraApertura(restauranteActualizado.getHoraApertura());
        restauranteExistente.setHoraCierre(restauranteActualizado.getHoraCierre());
        restauranteExistente.setActivo(restauranteActualizado.getActivo());
        
        return restauranteRepository.save(restauranteExistente);
    }

    public void eliminar(Long id) {
        Restaurante restaurante = obtenerPorId(id);
        restauranteRepository.delete(restaurante);
    }

    public void activar(Long id) {
        Restaurante restaurante = obtenerPorId(id);
        restaurante.setActivo(true);
        restauranteRepository.save(restaurante);
    }

    public void desactivar(Long id) {
        Restaurante restaurante = obtenerPorId(id);
        restaurante.setActivo(false);
        restauranteRepository.save(restaurante);
    }

    @Transactional(readOnly = true)
    public boolean existePorId(Long id) {
        return restauranteRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean estaAbierto(Long id) {
        return restauranteRepository.isRestauranteAbierto(id);
    }

    @Transactional(readOnly = true)
    public Long contarTotal() {
        return restauranteRepository.count();
    }

    @Transactional(readOnly = true)
    public Long contarActivos() {
        return restauranteRepository.countByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerMetricasBasicas(Long restauranteId) {
        Map<String, Object> metricas = new HashMap<>();
        
        metricas.put("totalMenus", restauranteRepository.countMenusByRestaurante(restauranteId));
        metricas.put("totalPedidos", restauranteRepository.countPedidosByRestaurante(restauranteId));
        metricas.put("promedioCalificacion", restauranteRepository.calcularPromedioCalificaciones(restauranteId));
        
        return metricas;
    }

    // Métodos de conversión
    private RestauranteDto convertirADto(Restaurante restaurante) {
        RestauranteDto dto = new RestauranteDto();
        dto.setId(restaurante.getId());
        dto.setNombre(restaurante.getNombre());
        dto.setDescripcion(restaurante.getDescripcion());
        dto.setDireccion(restaurante.getDireccion());
        dto.setTelefono(restaurante.getTelefono());
        dto.setHoraApertura(restaurante.getHoraApertura());
        dto.setHoraCierre(restaurante.getHoraCierre());
        dto.setActivo(restaurante.getActivo());
        dto.setClienteId(restaurante.getCliente().getId());
        dto.setClienteNombre(restaurante.getCliente().getNombre());
        dto.setFechaRegistro(restaurante.getFechaRegistro());
        return dto;
    }

    // Métodos adicionales requeridos por el controller
    public Page<RestauranteDto> listarRestaurantesActivos(Pageable pageable) {
        return listarActivosPaginado(pageable).map(this::convertirADto);
    }

    public Page<RestauranteDto> listarRestaurantes(Pageable pageable) {
        return listarPaginado(pageable).map(this::convertirADto);
    }

    public RestauranteDto obtenerRestaurantePorId(Long id) {
        return convertirADto(obtenerPorId(id));
    }

    public RestauranteDto crearRestaurante(RestauranteDto restauranteDto) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNombre(restauranteDto.getNombre());
        restaurante.setDescripcion(restauranteDto.getDescripcion());
        restaurante.setDireccion(restauranteDto.getDireccion());
        restaurante.setTelefono(restauranteDto.getTelefono());
        restaurante.setHoraApertura(restauranteDto.getHoraApertura());
        restaurante.setHoraCierre(restauranteDto.getHoraCierre());
        restaurante.setActivo(restauranteDto.getActivo() != null ? restauranteDto.getActivo() : true);
        
        // Buscar y asignar cliente
        Cliente cliente = clienteRepository.findById(restauranteDto.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + restauranteDto.getClienteId()));
        restaurante.setCliente(cliente);
        
        Restaurante resultado = restauranteRepository.save(restaurante);
        return convertirADto(resultado);
    }

    public RestauranteDto actualizarRestaurante(Long id, RestauranteDto restauranteDto) {
        Restaurante restauranteExistente = obtenerPorId(id);
        
        restauranteExistente.setNombre(restauranteDto.getNombre());
        restauranteExistente.setDescripcion(restauranteDto.getDescripcion());
        restauranteExistente.setDireccion(restauranteDto.getDireccion());
        restauranteExistente.setTelefono(restauranteDto.getTelefono());
        restauranteExistente.setHoraApertura(restauranteDto.getHoraApertura());
        restauranteExistente.setHoraCierre(restauranteDto.getHoraCierre());
        restauranteExistente.setActivo(restauranteDto.getActivo() != null ? restauranteDto.getActivo() : restauranteExistente.getActivo());
        
        Restaurante resultado = restauranteRepository.save(restauranteExistente);
        return convertirADto(resultado);
    }

    public void eliminarRestaurante(Long id) {
        eliminar(id);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerMetricasBasicas() {
        Map<String, Object> metricas = new HashMap<>();
        
        metricas.put("totalRestaurantes", restauranteRepository.count());
        metricas.put("restaurantesActivos", restauranteRepository.countByActivoTrue());
        metricas.put("restaurantesInactivos", restauranteRepository.count() - restauranteRepository.countByActivoTrue());
        
        return metricas;
    }

    public RestauranteDto cambiarEstado(Long id, boolean activo) {
        Restaurante restaurante = obtenerPorId(id);
        restaurante.setActivo(activo);
        Restaurante resultado = restauranteRepository.save(restaurante);
        return convertirADto(resultado);
    }
}