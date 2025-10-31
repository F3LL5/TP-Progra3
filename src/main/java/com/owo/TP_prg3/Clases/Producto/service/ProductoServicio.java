package com.owo.TP_prg3.Clases.Producto.service;

import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Producto.dto.FormProductoDTO;
import com.owo.TP_prg3.Clases.Producto.dto.ProductoDTO;
import com.owo.TP_prg3.Clases.Producto.modelo.Producto;
import com.owo.TP_prg3.Clases.Producto.modelo.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProductoServicio implements I_CRUD<Producto, ProductoDTO, FormProductoDTO, Long> {
    //Atributos
    @Autowired
    private ProductoRepositorio productoRepositorio;

    //Conversion
    @Override
    public Producto convertir_a_Obj(FormProductoDTO pDTO){
        return new Producto(
            null,
            pDTO.getNombre(),
            pDTO.getCategoria()
        );
    }
    @Override
    public ProductoDTO convertir_a_DTO(Producto producto){
        return new ProductoDTO(
                producto.getProducto_id(),
                producto.getNombre(),
                producto.getCategoria()
        );
    }
    

    //Metodos
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
    public boolean cargar(FormProductoDTO createItemDTO) {
        Producto producto = convertir_a_Obj(createItemDTO);
        producto = productoRepositorio.save(producto);
        return true;
    }

    @Override
    public boolean actualizar(Long id, FormProductoDTO formItemDTO) {
        Optional<Producto> optional = productoRepositorio.findById(id);
        if (optional.isEmpty()) return false;
        
        Producto producto = optional.get();
        producto.setNombre(formItemDTO.getNombre());
        producto.setCategoria(formItemDTO.getCategoria());

        productoRepositorio.save(producto);
        return true;
    }

    @Override
    public boolean eliminar(Long id) {
        Optional<Producto> optional = productoRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        else productoRepositorio.delete(optional.get());
        return true;
    }

    @Override
    public Set<ProductoDTO> filtrar(String campo, Object valor) {

        // 1. Obtenemos todos los productos
        Stream<Producto> stream = productoRepositorio.findAll().stream();

        // 2. Creamos un filtro dependiendo del campo
        Predicate<Producto> filtro = p -> false;
        switch (campo.toLowerCase()) {
            case "nombre" -> {
                filtro = p -> p.getNombre().equals( valor);
            }
            case "categoria" -> {
                filtro = p -> p.getCategoria().equals( valor);
            }
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
            case "nombre" -> comparador = Comparator.comparing(Producto::getNombre);
            case "categoria" -> comparador = Comparator.comparing(Producto::getNombre);
            default -> comparador = Comparator.comparing(Producto::getProducto_id);
        }
        // 3. Lo da vuelta si no es ascendente
        if (!ascendente) comparador = comparador.reversed();

        // 4. Aplicamos el comparador al Stream, mapeamos a DTO
        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

}
