package com.owo.TP_prg3.Clases.Lote.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Inventario.service.InventarioServicio;
import com.owo.TP_prg3.Clases.Lote.dto.FormLoteDTO;
import com.owo.TP_prg3.Clases.Lote.dto.LoteDTO;
import com.owo.TP_prg3.Clases.Lote.modelo.Lote;
import com.owo.TP_prg3.Clases.Lote.modelo.LoteRepositorio;
import com.owo.TP_prg3.Clases.Producto.modelo.Producto;
import com.owo.TP_prg3.Clases.Producto.service.ProductoServicio;

import jakarta.transaction.Transactional;

@Service
public class LoteServicio implements I_CRUD<Lote, LoteDTO, FormLoteDTO> {

    // ATRIBUTOS
    // ------------------------------------------------------------------------------------------------------------------------------------------------
    @Autowired
    private LoteRepositorio loteRepositorio;
    @Autowired
    private ProductoServicio productoServicio;
    @Autowired
    private InventarioServicio inventarioServicio;

    // CONVERSION
    // ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public Lote convertir_a_Obj(FormLoteDTO fDTO) {
        System.out.println(fDTO);
        if (fDTO.getProducto() == null) {
            throw new IllegalArgumentException("La información del producto es obligatoria para crear un lote.");
        }
        Producto producto = productoServicio.obtenerOCrearProducto(fDTO.getProducto());

        Lote lote = new Lote();

        lote.setCostoUnitario(fDTO.getCostoUnitario());
        lote.setCantidadDisponible(fDTO.getCantidadDisponible());
        lote.setFechaIngreso(fDTO.getFechaIngreso() != null ? fDTO.getFechaIngreso() : LocalDate.now());
        lote.setProducto(producto);

        return lote;
    }

    @Override
    public LoteDTO convertir_a_DTO(Lote lote) {
        return new LoteDTO(
                lote.getLote_id(),
                lote.getProducto(),
                lote.getCantidadDisponible(),
                lote.getCostoUnitario(),
                lote.getFechaIngreso());
    }

    // METODOS
    // ------------------------------------------------------------------------------------------------------------------------------------------------

    // GET
    @Override
    public Set<LoteDTO> obtenerTodos() {
        return loteRepositorio.findAll()
                .stream()
                .map(this::convertir_a_DTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<LoteDTO> buscarPorID(Long id) {
        return loteRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    @Override
    public Set<LoteDTO> filtrar(String campo, Object valor) {

        Stream<Lote> stream = loteRepositorio.findAll().stream();

        Predicate<Lote> filtro;
        switch (campo.toLowerCase()) {
            case "cantidad" -> filtro = l -> l.getCantidadDisponible().equals(valor);
            case "producto" -> filtro = l -> l.getProducto().getProductoId().equals(valor); // Filtra por id de producto
            case "costoUnitario" -> filtro = l -> l.getCostoUnitario().equals(valor);
            case "fechaIngreso" -> filtro = l -> l.getFechaIngreso().equals(valor);
            default -> filtro = l -> false;
        }
        return stream.filter(filtro)
                .map(this::convertir_a_DTO)
                .collect(Collectors.toSet());
    }

    public Set<LoteDTO> ordenar(String campo, boolean ascendente) {

        Stream<Lote> stream = loteRepositorio.findAll().stream();

        Comparator<Lote> comparador;
        switch (campo.toLowerCase()) {
            case "cantidad" -> comparador = Comparator.comparing(Lote::getCantidadDisponible);
            case "producto" ->
                comparador = Comparator.comparing(Lote::getProducto, Comparator.comparing(Producto::getProductoId));
            case "costoUnitario" -> comparador = Comparator.comparing(Lote::getCostoUnitario);
            case "fechaIngreso" -> comparador = Comparator.comparing(Lote::getFechaIngreso);
            default -> comparador = Comparator.comparing(Lote::getLote_id);
        }
        if (!ascendente)
            comparador = comparador.reversed();

        return stream.sorted(comparador)
                .map(this::convertir_a_DTO)
                .collect(Collectors.toSet());
    }

    // POST
    @Override
    @Transactional 
    public boolean cargar(FormLoteDTO cDTO) {
        Lote loteACrear = convertir_a_Obj(cDTO);
        registrarEntradaStock(loteACrear.getProducto(),loteACrear.getCantidadDisponible(),loteACrear.getCostoUnitario());
        return true;
    }

    // PUT
    @Override
    public boolean actualizar(Long id, FormLoteDTO updateDTO) {
        Optional<Lote> optional = loteRepositorio.findById(id);
        if (optional.isEmpty())
            return false;

        Lote lote = optional.get();
        lote.setCantidadDisponible(updateDTO.getCantidadDisponible());
        lote.setCostoUnitario(updateDTO.getCostoUnitario());
        lote.setFechaIngreso(updateDTO.getFechaIngreso());

        loteRepositorio.save(lote);
        return true;
    }

    // DELETE
    @Override
    public boolean eliminar(Long id) {
        Optional<Lote> optional = loteRepositorio.findById(id);
        if (optional.isEmpty())
            return false;
        else
            loteRepositorio.delete(optional.get());
        return true;
    }

    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // METODOS CONTROL DE STOCK Y MANEJO DE INVENTARIO
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // Método que implementa la búsqueda ordenada por FIFO directamente a la base de datos para evitar Stream complejos
    public Set<Lote> obtenerLotesDisponiblesFIFO(Long productoId) {
        return loteRepositorio
                .findByProducto_ProductoIdAndCantidadDisponibleGreaterThanOrderByFechaIngresoAsc(productoId, 0).stream()
                .collect(Collectors.toSet());
    }

    // Método que calcula y devuelve el stock para un Producto específico.
    public Integer obtenerStockPorProducto(Long productoId) {
        // Obtenemos todos los lotes del producto que tienen stock > 0
        List<Lote> lotesActivos = loteRepositorio.findByProducto_ProductoIdAndCantidadDisponibleGreaterThan(productoId,
                0);

        // Sumamos la cantidad de cada lote
        return lotesActivos.stream()
                .mapToInt(Lote::getCantidadDisponible)
                .sum();
    }

    public boolean existenLotesActivos(Long productoId) {
        return obtenerStockPorProducto(productoId) > 0;
    }

    @Transactional
public Lote registrarEntradaStock(Producto producto, int cantidad, BigDecimal costoUnitario) {
    // 1. Crear el nuevo Lote
    Lote nuevoLote = new Lote();
    nuevoLote.setProducto(producto);
    nuevoLote.setCantidadDisponible(cantidad);
    nuevoLote.setCostoUnitario(costoUnitario);
    nuevoLote.setFechaIngreso(LocalDate.now()); 
    nuevoLote = loteRepositorio.save(nuevoLote); // Guardamos el lote

    // 2. Ajustar stock
    inventarioServicio.ajustarStock(producto.getProductoId(), cantidad); 
    
    // 3. Notificar a InventarioServicio (actualizarCostoAdquisicion) después de recalcular el CPP.
    inventarioServicio.actualizarCostoPromedioPonderado(
        producto.getProductoId(), 
        costoUnitario, 
        cantidad
    );

    return nuevoLote;
}
}
