package com.owo.TP_prg3.Clases.Inventario.service;

import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Inventario.dto.FormInventarioDTO;
import com.owo.TP_prg3.Clases.Inventario.dto.InventarioDTO;
import com.owo.TP_prg3.Clases.Inventario.modelo.Inventario;
import com.owo.TP_prg3.Clases.Inventario.modelo.InventarioRepositorio;
import com.owo.TP_prg3.Clases.Lote.service.LoteServicio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class InventarioServicio implements I_CRUD<Inventario, InventarioDTO, FormInventarioDTO> {

    // ATRIBUTOS ------------------------------------------------------------------------------------------------------------------------------------------------
    @Autowired
    private InventarioRepositorio inventarioRepositorio;

    @Autowired
    @Lazy
    private LoteServicio loteServicio;

    // CONVERSION -----------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public InventarioDTO convertir_a_DTO(Inventario inventario) {
        // 1. Obtiene el stock total real sumando las cantidades de los Lotes activos.
        Integer stockActual = loteServicio.obtenerStockPorProducto(inventario.getProductoId());

        return new InventarioDTO(
            inventario.getInventario_id(),
            stockActual,
            inventario.getProductoId(),
            inventario.getStockMin(),
            inventario.getPrecioVenta(),
            inventario.getCostoAdquisicion()
        );
    }

    @Override
    public Inventario convertir_a_Obj(FormInventarioDTO formDTO) {
        return new Inventario(
            null, //El id lo autogenera la base de datos
            formDTO.getCantidad(),
            formDTO.getProducto_id(),
            formDTO.getStockMin(),
            formDTO.getPrecioVenta(),
            formDTO.getCostoAdquisicion()
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
            case "cantidad"-> filtro = i -> i.getCantidad().equals(valor); 
            case "producto_id"-> filtro = i -> i.getProductoId().equals(valor); 
            case "stockMin"-> filtro = i -> i.getStockMin().equals(valor); 
            case "precioVenta"-> filtro = i -> i.getPrecioVenta().equals(valor); 
            case "costoAdquisicion"-> filtro = i -> i.getCostoAdquisicion().equals(valor); 
            default-> filtro = i -> false;
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
            case "cantidad"-> comparador = Comparator.comparing(Inventario::getCantidad);
            case "producto_id"-> comparador = Comparator.comparing(Inventario::getProductoId);
            case "stockMin"-> comparador = Comparator.comparing(Inventario::getStockMin);
            case "precioVenta"-> comparador = Comparator.comparing(Inventario::getPrecioVenta);
            case "costoAdquisicion"-> comparador = Comparator.comparing(Inventario::getCostoAdquisicion);
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
        if(loteServicio.existenLotesActivos(optional.get().getProductoId())) return false; // No se puede eliminar si hay stock para ese producto.
        
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
        inventario.setCostoAdquisicion(updateDTO.getCostoAdquisicion());

        inventarioRepositorio.save(inventario);
        return true;
    }

    //Metodos logica del negocio
    @Transactional
    public void ajustarStock(Long productoId, int delta) {
        Inventario inventario = inventarioRepositorio.findByProductoId(productoId).orElseThrow(() -> new RuntimeException("Inventario no encontrado para Producto ID: " + productoId));
                
        // Cantidad se actualiza internamente y refleja el stock total
        inventario.setCantidad(inventario.getCantidad() + delta);
        inventarioRepositorio.save(inventario);
    }


    public BigDecimal obtenerPrecioVentaPorProductoId(Long productoId) {
    Inventario inventario = inventarioRepositorio.findByProductoId(productoId)
        .orElseThrow(() -> new RuntimeException("Precio de Venta no encontrado: Producto ID " + productoId + " no tiene registro de Inventario."));
    
    if (inventario.getPrecioVenta() == null) {
        throw new RuntimeException("El Producto ID " + productoId + " no tiene un precio de venta configurado.");
    }
    
    return inventario.getPrecioVenta();
    }

    /* 
        Calcula y actualiza el Costo Promedio Ponderado (CPP). 
        CPP = [ (Stock Anterior * Costo Anterior) + (Cantidad Nueva * Costo Nuevo) ] / (Stock Total Final) 
    */
    @Transactional
    public void actualizarCostoPromedioPonderado(Long productoId, BigDecimal costoNuevo, int cantidadNueva) {
        Inventario inventario = inventarioRepositorio.findByProductoId(productoId).orElseThrow(() -> new RuntimeException("Inventario no encontrado para Producto ID: " + productoId));

        BigDecimal stockTotalFinal = new BigDecimal(inventario.getCantidad()); 
        
        // Si el stock total es igual a la nueva cantidad, significa que era cero antes.
        if (stockTotalFinal.compareTo(new BigDecimal(cantidadNueva)) == 0) {
            inventario.setCostoAdquisicion(costoNuevo);
            inventarioRepositorio.save(inventario);
            return;
        }

        // Stock y Costo Anteriores
        BigDecimal stockAntesDeEstaEntrada = stockTotalFinal.subtract(new BigDecimal(cantidadNueva));
        BigDecimal costoAnterior = inventario.getCostoAdquisicion() != null ? inventario.getCostoAdquisicion() : BigDecimal.ZERO;

        // 1. Stock Anterior * Costo Anterior
        BigDecimal valorAnterior = stockAntesDeEstaEntrada.multiply(costoAnterior);

        // 2. Cantidad Nueva * Costo Nuevo
        BigDecimal valorNuevo = costoNuevo.multiply(new BigDecimal(cantidadNueva));

        // 3. Valorización Total
        BigDecimal valorTotal = valorAnterior.add(valorNuevo);

        // 4. Nuevo CPP: Valor Total / Stock Total Final
        BigDecimal nuevoCPP = valorTotal.divide(stockTotalFinal, 2, java.math.RoundingMode.HALF_UP);
        
        inventario.setCostoAdquisicion(nuevoCPP);
        inventarioRepositorio.save(inventario);
    } 

}
