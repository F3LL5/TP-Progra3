package com.owo.TP_prg3.Clases.DetallePedido.service;

import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.FormDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedido;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedidoRepositorio;
import com.owo.TP_prg3.Clases.Enum.TipoPedido;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Inventario.service.InventarioServicio;
import com.owo.TP_prg3.Clases.Lote.service.LoteServicio;
import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Pedido.modelo.PedidoRepositorio;
import com.owo.TP_prg3.Clases.Pedido.service.PedidoServicio;
import com.owo.TP_prg3.Clases.Producto.modelo.Producto;
import com.owo.TP_prg3.Clases.Producto.modelo.ProductoRepositorio;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.ValidacionGeneral;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DetallePedidoServicio implements I_CRUD<DetallePedido, DetallePedidoDTO, FormDetallePedidoDTO> {

    // ATRIBUTOS ------------------------------------------------------------------------------------------------------
    private static final String ENTIDAD = "DetallePedido";
    
    @Autowired
    private DetallePedidoRepositorio detallePedidoRepositorio;
    
    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
    private PedidoServicio pedidoServicio;
    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private InventarioServicio inventarioServicio;

    @Autowired
    @Lazy
    private LoteServicio loteServicio;

    // CONVERSION -----------------------------------------------------------------------------------------------------
    @Override
    public DetallePedido convertir_a_Obj(FormDetallePedidoDTO fDTO) {
        throw new UnsupportedOperationException("El DetallePedido debe crearse con un Pedido asociado. Use crearDetalleYAsociar().");
    }

    @Override
    public DetallePedidoDTO convertir_a_DTO(DetallePedido detalle) {
        return new DetallePedidoDTO(
            detalle.getDetallePedidoId(),
            detalle.getProducto().getProductoId(), 
            detalle.getProducto().getNombre(), 
            detalle.getCantidad(),
            detalle.getSubtotal()
        );
    }
    
    // LECTURA (Read) -------------------------------------------------------------------------------------------------
    @Override
    public Set<DetallePedidoDTO> obtenerTodos() {
        return detallePedidoRepositorio.findAll().stream()
                .map(this::convertir_a_DTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<DetallePedidoDTO> buscarPorID(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return detallePedidoRepositorio.findById(id)
                .map(this::convertir_a_DTO);
    }
    
    // Método para obtener todos los detalles de un Pedido específico
    public Set<DetallePedidoDTO> buscarPorPedidoId(Long pedidoId) {
        return detallePedidoRepositorio.findByPedido_PedidoId(pedidoId).stream()
                .map(this::convertir_a_DTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<DetallePedidoDTO> filtrar(String campo, Object valor) {
        Stream<DetallePedido> stream = detallePedidoRepositorio.findAll().stream();

        Predicate<DetallePedido> filtro;
        switch (campo.toLowerCase()) {
            case "cantidad" -> filtro = d -> d.getCantidad().equals(valor);
            case "subtotal" -> filtro = d -> d.getSubtotal().equals(valor);
            case "productoid" -> filtro = d -> d.getProducto().getProductoId().equals(Long.valueOf(valor.toString()));
            default -> filtro = p -> false;
        }

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    @Override
    public Set<DetallePedidoDTO> ordenar(String campo, boolean ascendente) {
        Stream<DetallePedido> stream = detallePedidoRepositorio.findAll().stream();

        Comparator<DetallePedido> comparador;
        switch (campo.toLowerCase()) {
            case "cantidad" -> comparador = Comparator.comparing(DetallePedido::getCantidad);
            case "subtotal" -> comparador = Comparator.comparing(DetallePedido::getSubtotal);
            default -> comparador = Comparator.comparing(DetallePedido::getDetallePedidoId);
        }
        
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    // ESCRITURA -----------------------------------------------------------------------------
    
    @Override
    @Transactional
    public boolean cargar(FormDetallePedidoDTO cDTO) {
        throw new UnsupportedOperationException("El DetallePedido debe crearse con un Pedido asociado. Use el servicio de Pedido.");
    }
    
    @Transactional
    public boolean agregarDetalleAPedido(Long pedidoId, FormDetallePedidoDTO detalleDTO) {
        if (detalleDTO.getCantidad() == null || detalleDTO.getCantidad() <= 0) throw new IngresoInvalidoException("La cantidad debe ser un valor positivo.");
        
        // 1. Obtener Pedido y Producto
        Pedido pedido = pedidoRepositorio.findById(pedidoId).orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));
        Producto producto = productoRepositorio.findById(detalleDTO.getProductoId()).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detalleDTO.getProductoId()));

        BigDecimal subtotalCalculado;
        
        // 2. STOCK Y PRECIO basada en TipoPedido
        if (pedido.getTipo() == TipoPedido.VENTA) {
            loteServicio.registrarSalidaStockFIFO(detalleDTO.getProductoId(), detalleDTO.getCantidad());
            subtotalCalculado = calcularSubtotal(detalleDTO.getProductoId(), detalleDTO.getCantidad()); 
            
        } else if (pedido.getTipo() == TipoPedido.COMPRA) {
            if (detalleDTO.getCostoUnitarioCompra() == null) 
                throw new IngresoInvalidoException("Se requiere 'costoUnitarioCompra' para pedidos de COMPRA.");

            // Registra el lote, ajusta el stock consolidado y recalcula el CPP.
            loteServicio.registrarEntradaStock(
                producto, 
                detalleDTO.getCantidad(), 
                detalleDTO.getCostoUnitarioCompra()
            );
            
            // El subtotal de la compra se calcula usando el costo unitario de adquisición
            subtotalCalculado = detalleDTO.getCostoUnitarioCompra().multiply(new BigDecimal(detalleDTO.getCantidad()));
            
        } else {
            throw new UnsupportedOperationException("Tipo de Pedido no soportado: " + pedido.getTipo());
        }

        // 3. Crear, vincular y guardar el Detalle de Pedido (Común a ambos tipos)
        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setProducto(producto);
        detalle.setCantidad(detalleDTO.getCantidad());
        detalle.setSubtotal(subtotalCalculado.setScale(2, RoundingMode.HALF_UP));
        
        detallePedidoRepositorio.save(detalle); 

        // 4. Recalcular el Total del Pedido (y actualizar el monto de la Transacción)
        pedidoServicio.recalcularTotal(pedido.getPedidoId()); 
        
        return true;
    }

    @Override
    @Transactional
    public boolean actualizar(Long id, FormDetallePedidoDTO updateDTO) {
        Optional<DetallePedido> optional = detallePedidoRepositorio.findById(id);
        if (optional.isEmpty()) return false;
        
        DetallePedido detalle = optional.get();
        Pedido pedido = detalle.getPedido();

        Integer oldCantidad = detalle.getCantidad(); // 1. Guardar la cantidad antigua
        Integer newCantidad = updateDTO.getCantidad();
        if (newCantidad == null || newCantidad <= 0) throw new IngresoInvalidoException("La cantidad debe ser un valor positivo.");
        
        if (pedido.getTipo() == TipoPedido.VENTA) {
            int diferencia = newCantidad - oldCantidad;
            if (diferencia != 0) {      
                if (diferencia > 0) {
                    loteServicio.registrarSalidaStockFIFO(detalle.getProducto().getProductoId(), diferencia);
                } else {

                    loteServicio.registrarEntradaStock(
                        detalle.getProducto(), 
                        Math.abs(diferencia), 
                        inventarioServicio.obtenerCostoPromedioPonderado(detalle.getProducto().getProductoId())
                    );
                }
            }
        }

        Producto producto = productoRepositorio.findById(updateDTO.getProductoId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + updateDTO.getProductoId()));
        
        detalle.setProducto(producto);
        detalle.setCantidad(newCantidad); // Usar la nueva cantidad

        BigDecimal subtotalCalculado = calcularSubtotal( updateDTO.getProductoId(), newCantidad );
        detalle.setSubtotal(subtotalCalculado.setScale(2, RoundingMode.HALF_UP));

        detallePedidoRepositorio.save(detalle);

        // Recalcular el Total del Pedido
        pedidoServicio.recalcularTotal(pedido.getPedidoId());
        
        return true;
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        DetallePedido detalle = obtenerDetallePedidoPorId(id);
        
        Pedido pedido = detalle.getPedido(); 
        
        detallePedidoRepositorio.delete(detalle);
        
        if (pedido.getDetalles() != null) {
            pedido.getDetalles().remove(detalle); 
        }

        pedidoServicio.recalcularTotal(pedido.getPedidoId());
        
        return true;
    }

    private BigDecimal calcularSubtotal(Long productoId, Integer cantidad) {
        ValidacionGeneral.validarIdValido(productoId);
        ValidacionGeneral.mayorACero(cantidad, "cantidad");
        
        BigDecimal precioUnitario = inventarioServicio.obtenerPrecioVentaPorProductoId(productoId);
        
        return precioUnitario
            .multiply(new BigDecimal(cantidad))
            .setScale(2, RoundingMode.HALF_UP);
    }


    @Transactional
    public DetallePedido crearDetalleYAsociar(Pedido pedido, FormDetallePedidoDTO fDTO) {
        validarDatosDetalle(fDTO);
        // 1. Obtener Producto por ID
        Producto producto = productoRepositorio.findById(fDTO.getProductoId())
            .orElseThrow(() -> new EntidadNoEncontradaException("Producto", fDTO.getProductoId()));
        
        // 2. Calcular Subtotal
        BigDecimal subtotal = calcularSubtotal(fDTO.getProductoId(), fDTO.getCantidad());
        
        // 3. Crear DetallePedido
        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido); 
        detalle.setProducto(producto);
        detalle.setCantidad(fDTO.getCantidad());
        detalle.setSubtotal(subtotal);
        
        // 4. Guardar Detalle y asociar al set del Pedido
        detalle = detallePedidoRepositorio.save(detalle);

        if (pedido.getTipo() == TipoPedido.VENTA) {
            loteServicio.registrarSalidaStockFIFO(fDTO.getProductoId(), fDTO.getCantidad());
        }

        return detalle;
    }
    
    // VALIDACIONES PRIVADAS
    private void validarDatosDetalle(FormDetallePedidoDTO dto) {
        if (dto == null) throw new IngresoInvalidoException("Los datos de " + ENTIDAD + " no pueden ser nulos");

        if (dto.getProductoId() == null) throw new CampoRequeridoException("productoId");
        if (dto.getCantidad() == null) throw new CampoRequeridoException("cantidad");
        
        ValidacionGeneral.validarIdValido(dto.getProductoId());
        ValidacionGeneral.mayorACero(dto.getCantidad(), "cantidad");
    }

    private DetallePedido obtenerDetallePedidoPorId(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return detallePedidoRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }
}
