package com.owo.TP_prg3.Clases.DetallePedido.service;

import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.FormDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedido;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedidoRepositorio;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Inventario.service.InventarioServicio;
import com.owo.TP_prg3.Clases.Lote.service.LoteServicio;
import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Pedido.modelo.PedidoRepositorio;
import com.owo.TP_prg3.Clases.Pedido.modelo.TipoPedido;
import com.owo.TP_prg3.Clases.Pedido.service.PedidoServicio;
import com.owo.TP_prg3.Clases.Producto.modelo.Producto;
import com.owo.TP_prg3.Clases.Producto.modelo.ProductoRepositorio;
import com.owo.TP_prg3.Clases.Producto.service.ProductoServicio;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private DetallePedidoRepositorio detallePedidoRepositorio;
    
    @Autowired
    private ProductoRepositorio productoRepositorio;
    @Autowired
    private ProductoServicio productoServicio;

    @Autowired
    private PedidoServicio pedidoServicio;
    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private InventarioServicio inventarioServicio;

    @Autowired
    private LoteServicio loteServicio;

    // CONVERSION -----------------------------------------------------------------------------------------------------
    @Override
    public DetallePedido convertir_a_Obj(FormDetallePedidoDTO fDTO) {
        Producto producto = productoRepositorio.findById(fDTO.getProductoId()).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + fDTO.getProductoId()));
        return new DetallePedido(
            producto,
            fDTO.getCantidad(),
            BigDecimal.ZERO
        );
    }

    @Override
    public DetallePedidoDTO convertir_a_DTO(DetallePedido detalle) {
        return new DetallePedidoDTO(
            detalle.getDetallePedidoId(),
            productoServicio.convertir_a_DTO(detalle.getProducto()), 
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
    public boolean cargar(FormDetallePedidoDTO createItemDTO) {
        throw new IngresoInvalidoException("No se puede utilizar este método en este tipo de crud. Probar con agregarDetalleAPedido()");
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
        
        Producto producto = productoRepositorio.findById(updateDTO.getProductoId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + updateDTO.getProductoId()));
        detalle.setProducto(producto);

        detalle.setCantidad(updateDTO.getCantidad());

        BigDecimal subtotalCalculado = calcularSubtotal( updateDTO.getProductoId(), updateDTO.getCantidad() );
        detalle.setSubtotal(subtotalCalculado);

        detallePedidoRepositorio.save(detalle);

        pedidoServicio.recalcularTotal(pedido.getPedidoId());
        
        return true;
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        Optional<DetallePedido> optional = detallePedidoRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        
        DetallePedido detalle = optional.get();
        Pedido pedido = detalle.getPedido(); 
        
        detallePedidoRepositorio.delete(detalle);
        
        if (pedido.getDetalles() != null) {
            pedido.getDetalles().remove(detalle); 
        }

        pedidoServicio.recalcularTotal(pedido.getPedidoId());
        
        return true;
    }

    private BigDecimal calcularSubtotal(Long productoId, Integer cantidad) {
    if (cantidad == null || cantidad <= 0) return BigDecimal.ZERO;
    
    BigDecimal precioUnitario = inventarioServicio.obtenerPrecioVentaPorProductoId(productoId);
    
    // Calcular subtotal (Precio * Cantidad)
    return precioUnitario
        .multiply(new BigDecimal(cantidad))
        .setScale(2, RoundingMode.HALF_UP); // Asegura precisión
    }


    @Transactional
    public DetallePedido crearDetalleYAsociar(Pedido pedido, FormDetallePedidoDTO fDTO) {
        // 1. Obtener Producto por ID
        Producto producto = productoRepositorio.findById(fDTO.getProductoId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + fDTO.getProductoId()));
        
        // 2. Calcular Subtotal
        BigDecimal subtotal = calcularSubtotal(fDTO.getProductoId(), fDTO.getCantidad());
        
        // 3. Crear DetallePedido
        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido); 
        detalle.setProducto(producto);
        detalle.setCantidad(fDTO.getCantidad());
        detalle.setSubtotal(subtotal);
        
        // 4. Guardar Detalle y asociar al set del Pedido (si usas Set en la entidad)
        DetallePedido detalleGuardado = detallePedidoRepositorio.save(detalle);

        return detalleGuardado;
    }
    
}
