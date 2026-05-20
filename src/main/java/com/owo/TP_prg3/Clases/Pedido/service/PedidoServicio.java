package com.owo.TP_prg3.Clases.Pedido.service;

import com.owo.TP_prg3.Clases.CuentaBancaria.service.CuentaBancariaServicio;
import com.owo.TP_prg3.Clases.DetallePedido.dto.FormDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedido;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedidoRepositorio;
import com.owo.TP_prg3.Clases.DetallePedido.service.DetallePedidoServicio;
import com.owo.TP_prg3.Clases.Enum.EstadoPedido;
import com.owo.TP_prg3.Clases.Enum.TipoPedido;
import com.owo.TP_prg3.Clases.Herramientas.ExcelExportService;
import com.owo.TP_prg3.Clases.Inventario.dto.InventarioDTO;
import com.owo.TP_prg3.Clases.Inventario.service.InventarioServicio;
import com.owo.TP_prg3.Clases.Lote.service.LoteServicio;
import com.owo.TP_prg3.Clases.Pedido.dto.*;
import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Pedido.modelo.PedidoRepositorio;
import com.owo.TP_prg3.Clases.Producto.modelo.ProductoRepositorio;
import com.owo.TP_prg3.Clases.Tienda.service.TiendaServicio;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;
import com.owo.TP_prg3.Clases.Transaccion.service.TransaccionServicio;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.ReglaNegocioException;
import com.owo.TP_prg3.Excepciones.ValidacionGeneral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PedidoServicio {

    //ATRIBUTOS

    private static final String ENTIDAD = "Pedido";

    @Autowired
    PedidoRepositorio pedidoRepositorio;
    
    @Autowired
    ProductoRepositorio productoRepositorio;

    @Autowired
    DetallePedidoRepositorio detallePedidoRepositorio;
    @Autowired
    @Lazy
    DetallePedidoServicio detallePedidoServicio;

    @Autowired
    TransaccionServicio transaccionServicio;

    @Autowired
    InventarioServicio inventarioServicio;

    @Autowired
    LoteServicio loteServicio;

    @Autowired
    TiendaServicio tiendaServicio;

    @Autowired
    CuentaBancariaServicio cuentaBancariaServicio;

    
    @Autowired
    private ExcelExportService excelExportService;

    

    //CONVERSION
    private FormDetallePedidoDTO convertirDetalleA_DTO(DetallePedido d) {
    
        // Por defecto, el costo de compra es null (VENTA).
        BigDecimal costoUnitario = null; 

        // Verificamos si el pedido asociado es de tipo COMPRA.
        if (d.getPedido() != null && d.getPedido().getTipo() == TipoPedido.COMPRA) {
            // Si es una COMPRA, el subtotal representa el costo de adquisición.
            // Lo calculamos (Subtotal / Cantidad).
            costoUnitario = d.getSubtotal().divide(
                new BigDecimal(d.getCantidad()), 
                4,
                RoundingMode.HALF_UP
            );
        }
 
        return new FormDetallePedidoDTO(
            d.getProducto().getProductoId(),
            d.getCantidad(),
            costoUnitario
        );
    }

    @Transactional
    public Pedido convertir_a_Obj(FormPedidoDTO fDTO) {
        validarDatosPedido(fDTO);
        
        // 1. Inicializar el Pedido (con total en 0.00)
        Pedido p = new Pedido();
        p.setTipo(fDTO.getTipo());
        p.setEstado(EstadoPedido.PENDIENTE);
        
        // 2. Crear la Transaccion base
        Transaccion t = transaccionServicio.convertir_a_Obj(fDTO.getTransaccion());
        
        // 4. Conectar la Transacción y guardar el Pedido
        p.setTransaccion(t);
        pedidoRepositorio.save(p); 
        
        return p;
    }
        

    public PedidoDTO convertir_a_DTO(Pedido pedido) {
        Set<FormDetallePedidoDTO> detallesDTO = pedido.getDetalles().stream()
                .map(this::convertirDetalleA_DTO)
                .collect(Collectors.toSet());
        
        return new PedidoDTO(
            pedido.getPedidoId(),
            transaccionServicio.convertir_a_DTO(pedido.getTransaccion()),
            pedido.getTipo(),
            detallesDTO,
            pedido.getEstado()
        );
    }
    
    // --- LECTURA (GET) ---
    
    public Set<PedidoDTO> obtenerTodos() {
        return pedidoRepositorio.findAll().stream()
            .map(this::convertir_a_DTO)
            .collect(Collectors.toSet());
    }

    public Optional<PedidoDTO> buscarPorID(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return pedidoRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    public Optional<Pedido> buscarPedidoPorID(Long id) {
        return pedidoRepositorio.findById(id);
    }


    public Set<PedidoDTO> filtrar(String campo, Object valor) {
        Stream<Pedido> stream = pedidoRepositorio.findAll().stream();

        Predicate<Pedido> filtro = switch (campo.toLowerCase()) {
            case "tipo" -> p -> p.getTipo().toString().equalsIgnoreCase(String.valueOf(valor));
            default -> p -> false;
        };

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<PedidoDTO> ordenar(String campo, boolean ascendente) {
        Stream<Pedido> stream = pedidoRepositorio.findAll().stream();
        Comparator<Pedido> comparador;

        comparador = switch (campo.toLowerCase()) {
            case "tipo" -> Comparator.comparing(Pedido::getTipo);
            default -> Comparator.comparing(Pedido::getPedidoId);
        };
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public byte[] exportar() {
        List<PedidoDTO> lista = List.copyOf(obtenerTodos());
        byte[] excel = excelExportService.generarExcel(lista, "Pedidos");
        return excel;
    }

    // --- ESCRITURA (POST) ---
    
    @Transactional
    public Pedido cargar(FormPedidoDTO cDTO) {
        validarDatosPedido(cDTO);

        Pedido pedido = convertir_a_Obj(cDTO);
        recalcularTotal(pedido.getPedidoId());
        return pedido;
    }

    // --- ACTUALIZACION (PUT) ---
    
    @Transactional
    public boolean actualizar(Long id, FormPedidoDTO updateDTO) {
        Optional<Pedido> optional = pedidoRepositorio.findById(id);
        if (optional.isEmpty()) return false;
        
        Pedido pedido = optional.get();
         
        if (pedido.getEstado() == EstadoPedido.FINALIZADO) {
            throw new ReglaNegocioException("No se puede modificar un pedido finalizado.");
        }

        // 1. Actualizar campos del Pedido
        pedido.setTipo(updateDTO.getTipo());

        // 2. Actualizar Transaccion asociada
        if (updateDTO.getTransaccion() != null) {
            transaccionServicio.actualizar(pedido.getTransaccion().getTransaccion_id(), updateDTO.getTransaccion());
        }

        pedidoRepositorio.save(pedido);
        return true;
    }

    // --- ELIMINACION (DELETE) ---
    @Transactional
    public boolean eliminar(Long id) {
        Pedido pedido = obtenerPedidoPorId(id);

        if (pedido.getEstado() == EstadoPedido.FINALIZADO) {
        throw new ReglaNegocioException("No se puede eliminar un pedido finalizado.");
    }

        detallePedidoRepositorio.deleteAll(pedido.getDetalles());
        pedidoRepositorio.delete(pedido);
        return true;
    }

    @Transactional
    public void recalcularTotal(Long pedidoId) {
        Pedido pedido = obtenerPedidoPorId(pedidoId);
        
        // 1. Recalcular el total sumando TODOS los subtotales, comenzando desde CERO (BigDecimal.ZERO).
        BigDecimal nuevoTotal = BigDecimal.ZERO;
        for (DetallePedido detalle : pedido.getDetalles()) {
            nuevoTotal = nuevoTotal.add(detalle.getSubtotal());
        }

        // 3. Actualizar Transaccion.monto
        Transaccion transaccion = pedido.getTransaccion();
        if (transaccion != null) {
            if (transaccion.getMonto().compareTo(nuevoTotal) != 0) { 
                transaccion.setMonto(nuevoTotal);
            }
        }
    }


    // FINAL BOSS. Si esto anda bien soy god, el pedido service tiene muchos repos y service que vincular
    @Transactional
    public boolean finalizarPedido(Long pedidoId) {
        Pedido pedido = obtenerPedidoPorId(pedidoId);

        if (pedido.getEstado() == EstadoPedido.FINALIZADO) {
            throw new ReglaNegocioException("El pedido ya está finalizado.");
        }

        //Logica para actualizar el stock
        for (DetallePedido detalle : pedido.getDetalles()) {
            Long productoId = detalle.getProducto().getProductoId();
            Integer cantidad = detalle.getCantidad();
            
            if (pedido.getTipo() == TipoPedido.VENTA) {
                // Registrar SALIDA de stock (VENTA)
                loteServicio.registrarSalidaStockFIFO(productoId, cantidad);
            } else if (pedido.getTipo() == TipoPedido.COMPRA) {
                // Registrar ENTRADA de stock (COMPRA)
                BigDecimal costoUnitario = detalle.getSubtotal().divide( 
                    new BigDecimal(cantidad), 
                    4,
                    RoundingMode.HALF_UP
                );
                loteServicio.registrarEntradaStock(
                    detalle.getProducto(), 
                    cantidad, 
                    costoUnitario
                );
            }
        }

        Transaccion transaccion = pedido.getTransaccion();
        if (transaccion == null) throw new ReglaNegocioException("El pedido no tiene una Transacción asociada.");
        
        BigDecimal monto = transaccion.getMonto(); 
        
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El monto de la Transacción debe ser positivo para finalizar el pedido.");
        }
          
        // Determina si es VENTA (entra dinero) o COMPRA (sale dinero)
        boolean esVenta = pedido.getTipo() == TipoPedido.VENTA;
        
        // El ID de la Transacción que representa la Caja/Cuenta Bancaria de la Tienda
        Long metodoPagoId = esVenta ? transaccion.getDestino_id() : transaccion.getOrigen_id();
        
        // Mueve el dinero según TipoTransaccion y TipoPedido (Acredita o Debita)
        switch (transaccion.getTipo()) {
            case EFECTIVO -> {
                if (esVenta) tiendaServicio.acreditarMontoCaja(metodoPagoId, monto);
                else tiendaServicio.debitarMontoCaja(metodoPagoId, monto);
            }
            case DEBITO -> {
                if (esVenta) cuentaBancariaServicio.acreditarMonto(metodoPagoId, monto);
                else cuentaBancariaServicio.debitarMonto(metodoPagoId, monto);
            }
            default -> throw new IngresoInvalidoException("Tipo de Transacción no soportado para la finalización del pedido: " + transaccion.getTipo());
        }

        pedido.setEstado(EstadoPedido.FINALIZADO);
        pedidoRepositorio.save(pedido);

        return true;
    }

    // VALIDACIONES PRIVADAS
    private void validarDatosPedido(FormPedidoDTO dto) {
        if (dto == null) throw new IngresoInvalidoException("Los datos de " + ENTIDAD + " no pueden ser nulos");

        if (dto.getTipo() == null) throw new CampoRequeridoException("tipo");
        if (dto.getTransaccion() == null) throw new CampoRequeridoException("transaccion");

    }

    private Pedido obtenerPedidoPorId(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return pedidoRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }
}
