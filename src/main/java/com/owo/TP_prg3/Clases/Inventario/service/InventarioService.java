package com.owo.TP_prg3.Clases.Inventario.service;

import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Inventario.dto.FormInventarioDTO;
import com.owo.TP_prg3.Clases.Inventario.dto.InventarioDTO;
import com.owo.TP_prg3.Clases.Inventario.modelo.Inventario;
import com.owo.TP_prg3.Clases.Inventario.modelo.InventarioRepositorio;

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
public class InventarioService implements I_CRUD<Inventario, InventarioDTO, FormInventarioDTO> {

    // ATRIBUTOS ------------------------------------------------------------------------------------------------------------------------------------------------
    @Autowired
    private InventarioRepositorio inventarioRepositorio;

    // CONVERSION ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public Inventario convertir_a_Obj(FormInventarioDTO formDTO) {
        return new Inventario(
            null,
            formDTO.getCantidad(),
            formDTO.getProducto_id(),
            formDTO.getStockMin(),
            formDTO.getPrecioVenta(),
            formDTO.getCostoAdquisicion()
        );
    }

    @Override
    public InventarioDTO convertir_a_DTO(Inventario inventario) {
        return new InventarioDTO(
            inventario.getInventario_id(),
            inventario.getCantidad(),
            inventario.getProducto_id(),
            inventario.getStockMin(),
            inventario.getPrecioVenta(),
            inventario.getCostoAdquisicion()
        );
    }

    // METODOS ------------------------------------------------------------------------------------------------------------------------------------------------

    // GET
    @Override
    public Set<InventarioDTO> obtenerTodos() {
        return inventarioRepositorio.findAll()
                .stream()
                .map(this::convertir_a_DTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<InventarioDTO> buscarPorID(Long id) {
        return inventarioRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    @Override
    public Set<InventarioDTO> filtrar(String campo, Object valor){
        Stream<Inventario> stream = inventarioRepositorio.findAll().stream();

        Predicate<Inventario> filtro;
        switch (campo.toLowerCase()) {
            case "cantidad" -> { filtro = i -> i.getCantidad().equals(valor); }
            case "producto_id" -> { filtro = i -> i.getProducto_id().equals(valor); }
            case "stockMin" -> { filtro = i -> i.getStockMin().equals(valor); }
            case "precioVenta" -> { filtro = i -> i.getPrecioVenta().equals(valor); }
            case "costoAdquisicion" -> { filtro = i -> i.getCostoAdquisicion().equals(valor); }
            default -> { filtro = i -> false; }
        }

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    @Override
    public Set<InventarioDTO> ordenar(String campo, boolean ascendente) {
        Stream<Inventario> stream = inventarioRepositorio.findAll().stream();

        Comparator<Inventario> comparador;
        switch (campo.toLowerCase()) {
            case "cantidad" -> comparador = Comparator.comparing(Inventario::getCantidad);
            case "producto_id" -> comparador = Comparator.comparing(Inventario::getProducto_id);
            case "stockMin" -> comparador = Comparator.comparing(Inventario::getProducto_id);
            case "precioVenta" -> comparador = Comparator.comparing(Inventario::getPrecioVenta);
            case "costoAdquisicion" -> comparador = Comparator.comparing(Inventario::getCostoAdquisicion);
            default -> comparador = Comparator.comparing(Inventario::getInventario_id);
        }
        
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    // POST
    @Override
    public boolean cargar(FormInventarioDTO createItemDTO) {
        Inventario inventario = convertir_a_Obj(createItemDTO);
        inventario = inventarioRepositorio.save(inventario);
        return true;
    }

    // DELETE
    @Override
    public boolean eliminar(Long id) {
        Optional<Inventario> optional = inventarioRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        else inventarioRepositorio.delete(optional.get());
        return true;
    }

    // PUT
    @Override
    @Transactional
    public boolean actualizar(Long id, FormInventarioDTO updateDTO) {
        Optional<Inventario> optional = inventarioRepositorio.findById(id);
        if (optional.isEmpty()) return false;
        
        Inventario inventario = optional.get();
        inventario.setPrecioVenta(updateDTO.getPrecioVenta());
        inventario.setStockMin(updateDTO.getStockMin());
        inventario.setCantidad(updateDTO.getCantidad());

        //Serio (capaz se cambia)
        inventario.setCostoAdquisicion(updateDTO.getCostoAdquisicion());
    
        inventarioRepositorio.save(inventario);
        return true;
    }
}
