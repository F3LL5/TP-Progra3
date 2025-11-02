package com.owo.TP_prg3.Clases.Producto.service;

import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Inventario.dto.FormInventarioDTO;
import com.owo.TP_prg3.Clases.Inventario.service.InventarioServicio;
import com.owo.TP_prg3.Clases.Producto.dto.FormProductoDTO;
import com.owo.TP_prg3.Clases.Producto.dto.ProductoDTO;
import com.owo.TP_prg3.Clases.Producto.modelo.Producto;
import com.owo.TP_prg3.Clases.Producto.modelo.ProductoRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProductoServicio implements I_CRUD<Producto, ProductoDTO, FormProductoDTO> {
    
    // ATRIBUTOS ------------------------------------------------------------------------------------------------------------------------------------------------
    @Autowired
    private ProductoRepositorio productoRepositorio;
    @Autowired
    private InventarioServicio inventarioService;

    // CONVERSION ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public Producto convertir_a_Obj(FormProductoDTO fDTO){
        return new Producto(
            null,
            fDTO.getNombre(),
            fDTO.getCategoria()
        );
    }
    @Override
    public ProductoDTO convertir_a_DTO(Producto producto){
        return new ProductoDTO(
                producto.getProductoId(),
                producto.getNombre(),
                producto.getCategoria()
        );
    }
    

    // METODOS ------------------------------------------------------------------------------------------------------------------------------------------------
    
    // GET
    @Override
    public Set<ProductoDTO> obtenerTodos() {
        return productoRepositorio.findAll()
                .stream()
                .map(this::convertir_a_DTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ProductoDTO> buscarPorID(Long id) {
        return productoRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    @Override
    public Set<ProductoDTO> filtrar(String campo, Object valor) {

        // 1. Obtenemos todos los productos
        Stream<Producto> stream = productoRepositorio.findAll().stream();

        // 2. Creamos un filtro dependiendo del campo
        Predicate<Producto> filtro;
        switch (campo.toLowerCase()) {
            case "nombre"-> filtro = p -> p.getNombre().equals(valor);
            case "categoria" -> filtro = p -> p.getCategoria().equals(valor);
            default -> filtro = p -> false;
        }

        // 3. Aplicamos el filtro al Stream, mapeamos a DTO
        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<ProductoDTO> ordenar(String campo, boolean ascendente) {
        // 1. Obtenemos todos los productos
        Stream<Producto> stream = productoRepositorio.findAll().stream();

        // 2. Creamos un Comparator dependiendo del campo
        Comparator<Producto> comparador;
        switch (campo.toLowerCase()) {
            case "nombre"-> comparador = Comparator.comparing(Producto::getNombre);
            case "categoria"-> comparador = Comparator.comparing(Producto::getNombre);
            default -> comparador = Comparator.comparing(Producto::getProductoId);
        }
        // 3. Lo da vuelta si no es ascendente
        if (!ascendente) comparador = comparador.reversed();

        // 4. Aplicamos el comparador al Stream, mapeamos a DTO
        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    // POST
    @Override
    @Transactional
    public boolean cargar(FormProductoDTO createDTO) {
        Producto p = productoRepositorio.save(convertir_a_Obj(createDTO));

        //Crea un inventario defualt a ese Producto
        return inventarioService.cargar(new FormInventarioDTO(p.getProductoId()));
    }

    // PUT
    @Override
    public boolean actualizar(Long id, FormProductoDTO updateDTO) {
        Optional<Producto> optional = productoRepositorio.findById(id);
        if (optional.isEmpty()) return false;
        
        Producto producto = optional.get();
        producto.setNombre(updateDTO.getNombre());
        producto.setCategoria(updateDTO.getCategoria());

        productoRepositorio.save(producto);
        return true;
    }

    // DELETE
    @Override
    public boolean eliminar(Long id) {
        Optional<Producto> optional = productoRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        else productoRepositorio.delete(optional.get());
        return true;
    }
}
