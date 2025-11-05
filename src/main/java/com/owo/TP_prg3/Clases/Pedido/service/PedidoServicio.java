package com.owo.TP_prg3.Clases.Pedido.service;

import com.owo.TP_prg3.Clases.DetallePedido.dto.FormDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedido;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedidoRepositorio;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Inventario.service.InventarioServicio;
import com.owo.TP_prg3.Clases.Pedido.dto.*;
import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Pedido.modelo.PedidoRepositorio;
import com.owo.TP_prg3.Clases.Producto.modelo.Producto;
import com.owo.TP_prg3.Clases.Producto.modelo.ProductoRepositorio;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;
import com.owo.TP_prg3.Clases.Transaccion.service.TransaccionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PedidoServicio implements I_CRUD<Pedido, PedidoDTO, FormPedidoDTO> {
    //ATRIBUTOS

    @Autowired
    PedidoRepositorio pedidoRepositorio;
    @Autowired
    ProductoRepositorio productoRepositorio;
    @Autowired
    DetallePedidoRepositorio detallePedidoRepositorio;
    @Autowired
    TransaccionServicio transaccionServicio;
    @Autowired
    InventarioServicio inventarioServicio;

    //CONVERSION
    private FormDetallePedidoDTO convertirDetalleA_DTO(DetallePedido d) {
        return new FormDetallePedidoDTO(
                d.getProducto().getProductoId(),
                d.getCantidad()
        );
    }

    @Override
    public Pedido convertir_a_Obj(FormPedidoDTO fDTO) {
        Pedido p = new Pedido();
        p.setPedidoId(null);
        p.setTipo(fDTO.getTipo());
        p.setRemitenteId(fDTO.getRemitenteId());
        
        // 1. Convertir y asignar la Transaccion
        Transaccion t = transaccionServicio.convertir_a_Obj(fDTO.getTransaccion());
        p.setTransaccion(t);

        return p;
    }

    @Override
    public PedidoDTO convertir_a_DTO(Pedido pedido) {
        Set<FormDetallePedidoDTO> detallesDTO = pedido.getDetalles().stream()
                .map(this::convertirDetalleA_DTO)
                .collect(Collectors.toSet());
        
        return new PedidoDTO(
            pedido.getPedidoId(),
            transaccionServicio.convertir_a_DTO(pedido.getTransaccion()),
            pedido.getTipo(),
            pedido.getFechaCreacion(),
            pedido.getTotal(),
            pedido.getRemitenteId(),
            detallesDTO
        );
    }
    
    // --- LECTURA (GET) ---
    
    @Override
    public Set<PedidoDTO> obtenerTodos() {
        return pedidoRepositorio.findAll().stream()
            .map(this::convertir_a_DTO)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<PedidoDTO> buscarPorID(Long id) {
        return pedidoRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    public Optional<Pedido> buscarPedidoPorID(Long id) {
        return pedidoRepositorio.findById(id);
    }

    @Override
    public Set<PedidoDTO> filtrar(String campo, Object valor) {
        Stream<Pedido> stream = pedidoRepositorio.findAll().stream();

        Predicate<Pedido> filtro = switch (campo.toLowerCase()) {
            case "tipo" -> p -> p.getTipo().toString().equalsIgnoreCase(String.valueOf(valor));
            case "remitenteid" -> p -> p.getRemitenteId().equals(Long.valueOf(String.valueOf(valor)));
            default -> p -> false;
        };

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    @Override
    public Set<PedidoDTO> ordenar(String campo, boolean ascendente) {
        Stream<Pedido> stream = pedidoRepositorio.findAll().stream();
        Comparator<Pedido> comparador;

        comparador = switch (campo.toLowerCase()) {
            case "total" -> Comparator.comparing(Pedido::getTotal);
            case "fecha" -> Comparator.comparing(Pedido::getFechaCreacion);
            case "tipo" -> Comparator.comparing(Pedido::getTipo);
            default -> Comparator.comparing(Pedido::getPedidoId);
        };
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    // --- ESCRITURA (POST) ---
    
    @Override
    @Transactional
    public boolean cargar(FormPedidoDTO cDTO) {
        Pedido pedido = convertir_a_Obj(cDTO);
        BigDecimal totalCalculado = BigDecimal.ZERO;

        if (cDTO.getDetalles() != null) {
            for (FormDetallePedidoDTO detalleDTO : cDTO.getDetalles()) {
                
                // 1. Obtener el Producto y verificar si existe
                Producto producto = productoRepositorio.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto con ID " + detalleDTO.getProductoId() + " no encontrado."));
                
                // 2. Obtener el precio de venta de ese inventario
                BigDecimal precioUnitario = inventarioServicio.obtenerPrecioVentaPorProductoId(detalleDTO.getProductoId());
                
                // 3. Calcula el subtotal
                BigDecimal subtotalCalculado = precioUnitario
                    .multiply(new BigDecimal(detalleDTO.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP); // Asegurar precisión de 2 decimales
                
                DetallePedido detalle = new DetallePedido();
                detalle.setProducto(producto);
                detalle.setCantidad(detalleDTO.getCantidad());
                
                // 4. Asignar el subtotal calculado
                detalle.setSubtotal(subtotalCalculado);
                
                // 5. Vinculacion bi direccional
                detalle.setPedido(pedido); 
                pedido.addDetalle(detalle);
                
                // 6. Suma al total
                totalCalculado = totalCalculado.add(subtotalCalculado);
            }
        }
        
        // 7. Asignar datos generados/calculados por el sistema
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setTotal(totalCalculado);
        
        pedidoRepositorio.save(pedido);
        return true;
    }

    // --- ACTUALIZACION (PUT) ---
    
    @Override
    @Transactional
    public boolean actualizar(Long id, FormPedidoDTO updateDTO) {
        Optional<Pedido> optional = pedidoRepositorio.findById(id);
        if (optional.isEmpty()) return false;

        Pedido pedido = optional.get();
        
        // 1. Actualizar campos del Pedido
        pedido.setTipo(updateDTO.getTipo());
        pedido.setRemitenteId(updateDTO.getRemitenteId());

        // 2. Actualizar Transaccion asociada
        if (updateDTO.getTransaccion() != null) {
            transaccionServicio.actualizar(pedido.getTransaccion().getTransaccion_id(), updateDTO.getTransaccion());
        }

        pedidoRepositorio.save(pedido);
        return true;
    }

    // --- ELIMINACION (DELETE) ---
    
    @Override
    public boolean eliminar(Long id) {
        Optional<Pedido> optional = pedidoRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        
        // Debido a CascadeType.ALL, al eliminar el Pedido, se eliminará:
        // 1. Todos los DetallePedido asociados.
        // 2. La Transaccion asociada.
        pedidoRepositorio.delete(optional.get());
        return true;
    }

    public void recalcularTotal(Pedido pedido) {
        // Si el set de detalles es nulo, o si está vacío, el total es cero.
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            pedido.setTotal(BigDecimal.ZERO);
        } else {
            BigDecimal nuevoTotal = pedido.getDetalles().stream()
                .map(d -> d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO) 
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            pedido.setTotal(nuevoTotal);
        }
        pedidoRepositorio.save(pedido);

        
    }
}
