package com.delivery.sistema.delivery.y.gestion.restaurante.service;

import com.delivery.sistema.delivery.y.gestion.restaurante.model.Menu;
import com.delivery.sistema.delivery.y.gestion.restaurante.dto.MenuDto;
import com.delivery.sistema.delivery.y.gestion.restaurante.model.Categoria;
import com.delivery.sistema.delivery.y.gestion.restaurante.model.Restaurante;
import com.delivery.sistema.delivery.y.gestion.restaurante.repository.MenuRepository;
import com.delivery.sistema.delivery.y.gestion.restaurante.repository.CategoriaRepository;
import com.delivery.sistema.delivery.y.gestion.restaurante.repository.RestauranteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final CategoriaRepository categoriaRepository;
    private final RestauranteRepository restauranteRepository;

    public List<Menu> listarTodos() {
        return menuRepository.findAll();
    }

    public List<Menu> listarDisponibles() {
        return menuRepository.findByDisponibleTrueOrderByNombreAsc();
    }

    public List<Menu> listarPorRestaurante(Long restauranteId) {
        return menuRepository.findByRestauranteIdOrderByNombreAsc(restauranteId);
    }

    public List<Menu> listarDisponiblesPorRestaurante(Long restauranteId) {
        return menuRepository.findByRestauranteIdAndDisponibleTrueOrderByNombreAsc(restauranteId);
    }

    public List<Menu> listarPorCategoria(Long categoriaId) {
        return menuRepository.findByCategoriaIdOrderByNombreAsc(categoriaId);
    }

    public Page<Menu> listarPaginado(Pageable pageable) {
        return menuRepository.findAll(pageable);
    }

    public Page<Menu> listarPorRestaurantePaginado(Long restauranteId, Pageable pageable) {
        return menuRepository.findByRestauranteId(restauranteId, pageable);
    }

    public Optional<Menu> buscarPorId(Long id) {
        return menuRepository.findById(id);
    }

    public Menu obtenerPorId(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menú no encontrado con ID: " + id));
    }

    public List<Menu> buscarPorNombre(String nombre) {
        return menuRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Menu> buscarPorRangoPrecio(BigDecimal precioMin, BigDecimal precioMax) {
        return menuRepository.findByPrecioBetweenOrderByPrecioAsc(precioMin, precioMax);
    }

    public Menu crear(Menu menu) {
        // Validar que la categoría existe
        Categoria categoria = categoriaRepository.findById(menu.getCategoria().getId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + menu.getCategoria().getId()));
        
        // Validar que el restaurante existe
        Restaurante restaurante = restauranteRepository.findById(menu.getRestaurante().getId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante no encontrado con ID: " + menu.getRestaurante().getId()));
        
        menu.setCategoria(categoria);
        menu.setRestaurante(restaurante);
        
        return menuRepository.save(menu);
    }

    public Menu actualizar(Long id, Menu menuActualizado) {
        Menu menuExistente = obtenerPorId(id);
        
        // Validar y actualizar categoría si cambió
        if (!menuExistente.getCategoria().getId().equals(menuActualizado.getCategoria().getId())) {
            Categoria categoria = categoriaRepository.findById(menuActualizado.getCategoria().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + menuActualizado.getCategoria().getId()));
            menuExistente.setCategoria(categoria);
        }
        
        // Actualizar campos
        menuExistente.setNombre(menuActualizado.getNombre());
        menuExistente.setDescripcion(menuActualizado.getDescripcion());
        menuExistente.setPrecio(menuActualizado.getPrecio());
        menuExistente.setImagenUrl(menuActualizado.getImagenUrl());
        menuExistente.setDisponible(menuActualizado.getDisponible());
        
        return menuRepository.save(menuExistente);
    }

    public void eliminar(Long id) {
        Menu menu = obtenerPorId(id);
        menuRepository.delete(menu);
    }

    public void activar(Long id) {
        Menu menu = obtenerPorId(id);
        menu.setDisponible(true);
        menuRepository.save(menu);
    }

    public void desactivar(Long id) {
        Menu menu = obtenerPorId(id);
        menu.setDisponible(false);
        menuRepository.save(menu);
    }

    public void actualizarPrecio(Long id, BigDecimal nuevoPrecio) {
        Menu menu = obtenerPorId(id);
        menu.setPrecio(nuevoPrecio);
        menuRepository.save(menu);
    }

    public void actualizarImagen(Long id, String imagenUrl) {
        Menu menu = obtenerPorId(id);
        menu.setImagenUrl(imagenUrl);
        menuRepository.save(menu);
    }

    @Transactional(readOnly = true)
    public boolean existePorId(Long id) {
        return menuRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public Long contarPorRestaurante(Long restauranteId) {
        return menuRepository.countByRestauranteId(restauranteId);
    }

    @Transactional(readOnly = true)
    public Long contarDisponiblesPorRestaurante(Long restauranteId) {
        return menuRepository.countByRestauranteIdAndDisponibleTrue(restauranteId);
    }

    @Transactional(readOnly = true)
    public BigDecimal obtenerPrecioPromedioPorRestaurante(Long restauranteId) {
        return menuRepository.calcularPrecioPromedioPorRestaurante(restauranteId)
                .orElse(BigDecimal.ZERO);
    }

    // Métodos de conversión
    private MenuDto convertirADto(Menu menu) {
        MenuDto dto = new MenuDto();
        dto.setId(menu.getId());
        dto.setNombre(menu.getNombre());
        dto.setDescripcion(menu.getDescripcion());
        dto.setPrecio(menu.getPrecio());
        dto.setImagenUrl(menu.getImagenUrl());
        dto.setDisponible(menu.getDisponible());
        dto.setCategoriaId(menu.getCategoria().getId());
        dto.setCategoriaNombre(menu.getCategoria().getNombre());
        dto.setRestauranteId(menu.getRestaurante().getId());
        dto.setRestauranteNombre(menu.getRestaurante().getNombre());
        return dto;
    }

    // Métodos adicionales requeridos por el controller
    public Page<MenuDto> listarMenus(Pageable pageable) {
        return listarPaginado(pageable).map(this::convertirADto);
    }

    public Page<MenuDto> listarMenusPorRestaurante(Long restauranteId, Pageable pageable) {
        return listarPorRestaurantePaginado(restauranteId, pageable).map(this::convertirADto);
    }

    public MenuDto obtenerMenuPorId(Long id) {
        return convertirADto(obtenerPorId(id));
    }

    public MenuDto crearMenu(MenuDto menuDto) {
        Menu menu = new Menu();
        menu.setNombre(menuDto.getNombre());
        menu.setDescripcion(menuDto.getDescripcion());
        menu.setPrecio(menuDto.getPrecio());
        menu.setImagenUrl(menuDto.getImagenUrl());
        menu.setDisponible(menuDto.getDisponible() != null ? menuDto.getDisponible() : true);
        
        // Buscar y asignar categoria
        Categoria categoria = categoriaRepository.findById(menuDto.getCategoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + menuDto.getCategoriaId()));
        menu.setCategoria(categoria);
        
        // Buscar y asignar restaurante
        Restaurante restaurante = restauranteRepository.findById(menuDto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante no encontrado con ID: " + menuDto.getRestauranteId()));
        menu.setRestaurante(restaurante);
        
        Menu resultado = menuRepository.save(menu);
        return convertirADto(resultado);
    }

    public MenuDto actualizarMenu(Long id, MenuDto menuDto) {
        Menu menuExistente = obtenerPorId(id);
        
        menuExistente.setNombre(menuDto.getNombre());
        menuExistente.setDescripcion(menuDto.getDescripcion());
        menuExistente.setPrecio(menuDto.getPrecio());
        menuExistente.setImagenUrl(menuDto.getImagenUrl());
        menuExistente.setDisponible(menuDto.getDisponible() != null ? menuDto.getDisponible() : menuExistente.getDisponible());
        
        // Actualizar categoría si cambió
        if (menuDto.getCategoriaId() != null && !menuExistente.getCategoria().getId().equals(menuDto.getCategoriaId())) {
            Categoria categoria = categoriaRepository.findById(menuDto.getCategoriaId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + menuDto.getCategoriaId()));
            menuExistente.setCategoria(categoria);
        }
        
        Menu resultado = menuRepository.save(menuExistente);
        return convertirADto(resultado);
    }

    public void eliminarMenu(Long id) {
        eliminar(id);
    }

    public String subirImagen(Long id, org.springframework.web.multipart.MultipartFile imagen) {
        // Por ahora, una implementación básica que solo actualiza la URL
        // En un ambiente real, aquí subirías la imagen a un servicio como AWS S3
        Menu menu = obtenerPorId(id);
        String imagenUrl = "/imagenes/menu/" + id + "_" + imagen.getOriginalFilename();
        menu.setImagenUrl(imagenUrl);
        menuRepository.save(menu);
        return imagenUrl;
    }

    public MenuDto cambiarDisponibilidad(Long id, boolean disponible) {
        Menu menu = obtenerPorId(id);
        menu.setDisponible(disponible);
        Menu resultado = menuRepository.save(menu);
        return convertirADto(resultado);
    }

    // Métodos adicionales para el controller
    public Page<MenuDto> listarMenusDisponiblesPorRestaurante(Long restauranteId, Pageable pageable) {
        return menuRepository.findByRestauranteIdAndDisponibleTrue(restauranteId, pageable).map(this::convertirADto);
    }

    public Page<MenuDto> listarMenusPorCategoria(Long categoriaId, Pageable pageable) {
        return menuRepository.findByCategoriaId(categoriaId, pageable).map(this::convertirADto);
    }
}