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

import com.owo.TP_prg3.Clases.Herramientas.ExcelExportService;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Inventario.dto.InventarioDTO;
import com.owo.TP_prg3.Clases.Inventario.service.InventarioServicio;
import com.owo.TP_prg3.Clases.Lote.dto.FormLoteDTO;
import com.owo.TP_prg3.Clases.Lote.dto.LoteDTO;
import com.owo.TP_prg3.Clases.Lote.modelo.Lote;
import com.owo.TP_prg3.Clases.Lote.modelo.LoteRepositorio;
import com.owo.TP_prg3.Clases.Producto.modelo.Producto;
import com.owo.TP_prg3.Clases.Producto.service.ProductoServicio;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.OperacionNoPermitidaException;
import com.owo.TP_prg3.Excepciones.StockInsuficienteException;
import com.owo.TP_prg3.Excepciones.ValidacionGeneral;

import jakarta.transaction.Transactional;

@Service
public class LoteServicio implements I_CRUD<Lote, LoteDTO, FormLoteDTO> {

    // ATRIBUTOS
    // ------------------------------------------------------------------------------------------------------------------------------------------------
    private static final String ENTIDAD = "Lote";

    @Autowired
    private LoteRepositorio loteRepositorio;
    @Autowired
    private ProductoServicio productoServicio;
    @Autowired
    private InventarioServicio inventarioServicio;

    
    @Autowired
    private ExcelExportService excelExportService;

    // CONVERSION
    // ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public Lote convertir_a_Obj(FormLoteDTO fDTO) {
        validarDatosLote(fDTO);
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
        ValidacionGeneral.validarIdValido(id);
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

       public byte[] exportar() {
        List<LoteDTO> lista = List.copyOf(obtenerTodos());
        byte[] excel = excelExportService.generarExcel(lista, "Lotes");
        return excel;
    }

    // POST
    @Override
    @Transactional 
    public boolean cargar(FormLoteDTO cDTO) {
        validarDatosLote(cDTO);
        Lote loteACrear = convertir_a_Obj(cDTO);
        registrarEntradaStock(loteACrear.getProducto(),loteACrear.getCantidadDisponible(),loteACrear.getCostoUnitario());
        return true;
    }

    // PUT
    @Override
    @Transactional
    public boolean actualizar(Long id, FormLoteDTO updateDTO) {
        validarDatosLote(updateDTO);
        Lote lote = obtenerLotePorId(id);

        if (updateDTO.getCantidadDisponible() < lote.getCantidadDisponible()) {
            throw new OperacionNoPermitidaException("No se permite reducir la cantidad disponible de un " + ENTIDAD + " directamente por actualización. Use el método de consumo de stock.");
        }

        lote.setCantidadDisponible(updateDTO.getCantidadDisponible());
        lote.setCostoUnitario(updateDTO.getCostoUnitario());
        lote.setFechaIngreso(updateDTO.getFechaIngreso());

        loteRepositorio.save(lote);
        return true;
    }

    // DELETE
    @Override
    @Transactional
    public boolean eliminar(Long id) {
        Lote lote = obtenerLotePorId(id);
        validarLotePuedeEliminarse(id);

        loteRepositorio.delete(lote);
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
        ValidacionGeneral.validarIdValido(productoId);
        List<Lote> lotesActivos = loteRepositorio.findByProducto_ProductoIdAndCantidadDisponibleGreaterThan(productoId,0);

        return lotesActivos.stream()
                .mapToInt(Lote::getCantidadDisponible)
                .sum();
    }

    public boolean existenLotesActivos(Long productoId) {
        return obtenerStockPorProducto(productoId) > 0;
    }

    @Transactional
    public Lote registrarEntradaStock(Producto producto, int cantidad, BigDecimal costoUnitario) {
        ValidacionGeneral.mayorACero(cantidad, "cantidad");

        // 1. Crear el nuevo Lote
        Lote nuevoLote = new Lote();
        nuevoLote.setProducto(producto);
        nuevoLote.setCantidadDisponible(cantidad);
        nuevoLote.setCostoUnitario(costoUnitario);
        nuevoLote.setFechaIngreso(LocalDate.now());

        nuevoLote = loteRepositorio.save(nuevoLote); // Guardamos el lote

        // Ajustar stock
        inventarioServicio.ajustarStock(producto.getProductoId(), cantidad); 
        
        // Actualizar el costo
        inventarioServicio.actualizarCostoPromedioPonderado(producto.getProductoId(), costoUnitario, cantidad);

        return nuevoLote;
    }

    @Transactional
    public void registrarSalidaStockFIFO(Long productoId, int cantidadARetirar) {
        ValidacionGeneral.validarIdValido(productoId);
        if (cantidadARetirar <= 0) return;

        // 1. Verificar si hay suficiente stock consolidado
        Integer stockActual = obtenerStockPorProducto(productoId);
        if (stockActual < cantidadARetirar) throw new StockInsuficienteException("Stock insuficiente. Se solicitó " + cantidadARetirar + " unidades, pero solo hay " + stockActual + " disponibles.");


        Set<Lote> lotesFIFO = obtenerLotesDisponiblesFIFO(productoId);
        int restante = cantidadARetirar;

        // 3. Consumir lotes por orden FIFO
        for (Lote lote : lotesFIFO) {
            if (restante <= 0) break; 
            
            int cantidadLote = lote.getCantidadDisponible();
            int cantidadAUsar = Math.min(cantidadLote, restante);

            // Actualizar el lote
            lote.setCantidadDisponible(cantidadLote - cantidadAUsar);
            loteRepositorio.save(lote);

            // Reducir la cantidad restante
            restante -= cantidadAUsar;
        }

        // Ajustar stock
        inventarioServicio.ajustarStock(productoId, -cantidadARetirar);
    }

    // VALIDACIONES PRIVADAS
    private void validarDatosLote(FormLoteDTO dto) {
        if (dto == null) throw new IngresoInvalidoException("Los datos de " + ENTIDAD + " no pueden ser nulos");

        if (dto.getProducto() == null) throw new CampoRequeridoException("producto");
        if (dto.getCantidadDisponible() == null) throw new CampoRequeridoException("cantidadDisponible");
        if (dto.getCostoUnitario() == null) throw new CampoRequeridoException("costoUnitario");

        ValidacionGeneral.noNegativo(dto.getCantidadDisponible(), "cantidadDisponible");
        ValidacionGeneral.noNegativo(dto.getCostoUnitario(), "costoUnitario");
        if (dto.getFechaIngreso() != null && dto.getFechaIngreso().isAfter(LocalDate.now())) {
            throw new IngresoInvalidoException("fechaIngreso", "no puede ser una fecha futura");
        }
    }

    private Lote obtenerLotePorId(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return loteRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }

    private void validarLotePuedeEliminarse(Long loteId) {
        Optional<Lote> optional = loteRepositorio.findById(loteId);
        if (optional.isEmpty()) throw new EntidadNoEncontradaException(ENTIDAD, loteId);
    }
}
