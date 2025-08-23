package com.delivery.sistema.delivery.y.gestion.restaurante.service;

import com.delivery.sistema.delivery.y.gestion.restaurante.model.Categoria;
import com.delivery.sistema.delivery.y.gestion.restaurante.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public List<Categoria> listarActivas() {
        return categoriaRepository.findByActivoTrueOrderByOrdenMostrarAsc();
    }

    public List<Categoria> listarCategoriasConMenus() {
        return categoriaRepository.findCategoriasConMenus();
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public Categoria obtenerPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + id));
    }

    public Optional<Categoria> buscarPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre);
    }

    public List<Categoria> buscarPorNombreParcial(String nombre) {
        return categoriaRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    public Categoria crear(Categoria categoria) {
        // Validar que no exista una categoría con el mismo nombre
        if (categoriaRepository.existsByNombre(categoria.getNombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }

        // Si no se especifica orden, asignar el siguiente disponible
        if (categoria.getOrdenMostrar() == null || categoria.getOrdenMostrar() == 0) {
            Integer siguienteOrden = categoriaRepository.findCategoriaConMayorOrden()
                    .map(cat -> cat.getOrdenMostrar() + 1)
                    .orElse(1);
            categoria.setOrdenMostrar(siguienteOrden);
        }

        return categoriaRepository.save(categoria);
    }

    public Categoria actualizar(Long id, Categoria categoriaActualizada) {
        Categoria categoriaExistente = obtenerPorId(id);

        // Validar que no exista otra categoría con el mismo nombre
        if (!categoriaExistente.getNombre().equals(categoriaActualizada.getNombre()) &&
            categoriaRepository.existsByNombre(categoriaActualizada.getNombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoriaActualizada.getNombre());
        }

        // Actualizar campos
        categoriaExistente.setNombre(categoriaActualizada.getNombre());
        categoriaExistente.setDescripcion(categoriaActualizada.getDescripcion());
        categoriaExistente.setActivo(categoriaActualizada.getActivo());
        categoriaExistente.setOrdenMostrar(categoriaActualizada.getOrdenMostrar());

        return categoriaRepository.save(categoriaExistente);
    }

    public void eliminar(Long id) {
        Categoria categoria = obtenerPorId(id);
        
        // Verificar que no tenga menús asociados
        Long cantidadMenus = categoriaRepository.countMenusByCategoria(id);
        if (cantidadMenus > 0) {
            throw new IllegalStateException("No se puede eliminar la categoría porque tiene " + cantidadMenus + " menús asociados");
        }

        categoriaRepository.delete(categoria);
    }

    public void activar(Long id) {
        Categoria categoria = obtenerPorId(id);
        categoria.setActivo(true);
        categoriaRepository.save(categoria);
    }

    public void desactivar(Long id) {
        Categoria categoria = obtenerPorId(id);
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
    }

    public void cambiarOrden(Long id, Integer nuevoOrden) {
        Categoria categoria = obtenerPorId(id);
        categoria.setOrdenMostrar(nuevoOrden);
        categoriaRepository.save(categoria);
    }

    @Transactional(readOnly = true)
    public boolean existePorId(Long id) {
        return categoriaRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existePorNombre(String nombre) {
        return categoriaRepository.existsByNombre(nombre);
    }

    @Transactional(readOnly = true)
    public Long contarMenusEnCategoria(Long categoriaId) {
        return categoriaRepository.countMenusByCategoria(categoriaId);
    }

    @Transactional(readOnly = true)
    public Categoria obtenerConMenus(Long id) {
        return categoriaRepository.findByIdWithMenus(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + id));
    }
}