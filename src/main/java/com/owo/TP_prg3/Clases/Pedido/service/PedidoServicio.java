package com.owo.TP_prg3.Clases.Pedido.service;

import com.owo.TP_prg3.Clases.CuentaBancaria.service.CuentaBancariaServicio;
import com.owo.TP_prg3.Clases.DetallePedido.dto.FormDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedido;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedidoRepositorio;
import com.owo.TP_prg3.Clases.DetallePedido.service.DetallePedidoServicio;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Inventario.service.InventarioServicio;
import com.owo.TP_prg3.Clases.Pedido.dto.*;
import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Pedido.modelo.PedidoRepositorio;
import com.owo.TP_prg3.Clases.Pedido.modelo.TipoPedido;
import com.owo.TP_prg3.Clases.Producto.modelo.ProductoRepositorio;
import com.owo.TP_prg3.Clases.Tienda.service.TiendaServicio;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;
import com.owo.TP_prg3.Clases.Transaccion.service.TransaccionServicio;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    @Lazy
    DetallePedidoServicio detallePedidoServicio;

    @Autowired
    TransaccionServicio transaccionServicio;
    @Autowired
    InventarioServicio inventarioServicio;
    @Autowired
    TiendaServicio tiendaServicio;
    @Autowired
    CuentaBancariaServicio cuentaBancariaServicio;

    //CONVERSION
    private FormDetallePedidoDTO convertirDetalleA_DTO(DetallePedido d) {
    
        // Por defecto, el costo de compra es null (VENTA).
        BigDecimal costoUnitarioCompra = null; 

        // Verificamos si el pedido asociado es de tipo COMPRA.
        if (d.getPedido() != null && d.getPedido().getTipo() == TipoPedido.COMPRA) {
            // Si es una COMPRA, el subtotal representa el costo de adquisición.
            // Lo calculamos (Subtotal / Cantidad).
            costoUnitarioCompra = d.getSubtotal().divide(
                new BigDecimal(d.getCantidad()), 
                4,
                RoundingMode.HALF_UP
            );
        }
 
        return new FormDetallePedidoDTO(
            d.getProducto().getProductoId(),
            d.getCantidad(),
            costoUnitarioCompra
        );
    }

    @Override
    @Transactional
    public Pedido convertir_a_Obj(FormPedidoDTO fDTO) {
        
        // 1. Inicializar el Pedido (con total en 0.00)
        Pedido p = new Pedido();
        p.setTipo(fDTO.getTipo());
        p.setRemitenteId(fDTO.getRemitenteId());
        p.setDestinatarioId(fDTO.getDestinatarioId());
        p.setTotal(BigDecimal.ZERO);
        
        // 2. Crear la Transaccion base
        Transaccion t = transaccionServicio.convertir_a_Obj(fDTO.getTransaccion());

        // 3. Completar el ID faltante (Cliente o Proveedor)
        if (fDTO.getTipo() == TipoPedido.VENTA) t.setOrigen_id(fDTO.getRemitenteId()); // Origen debe ser el ID del Cliente (RemitenteId).
        else t.setDestino_id(fDTO.getDestinatarioId());
        
        // 4. Conectar la Transacción y guardar el Pedido
        p.setTransaccion(t);
        pedidoRepositorio.save(p); 
        
        // 5. Cargar Detalles y Recalcular Total (necesario para un Pedido de creación)
        if (fDTO.getDetalles() != null && !fDTO.getDetalles().isEmpty()) {
            fDTO.getDetalles().forEach(formDetalle -> {
                detallePedidoServicio.crearDetalleYAsociar(p, formDetalle);
            });
        }
        
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
        // 1. Validar
        if (cDTO.getTipo() == null || cDTO.getRemitenteId() == null || cDTO.getTransaccion() == null) throw new IngresoInvalidoException("El Tipo de Pedido, Remitente y la información base de la Transacción son obligatorios para iniciar un Pedido.");
        
        Pedido pedido = convertir_a_Obj(cDTO);
        
        // 3. Asignar datos generados
        pedido.setFechaCreacion(LocalDateTime.now());


        recalcularTotal(pedido.getPedidoId());
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

    public void recalcularTotal(Long pedidoId) {
        Pedido pedido = pedidoRepositorio.findById(pedidoId).orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));
        
        // 1. Recalcular el total sumando TODOS los subtotales, comenzando desde CERO (BigDecimal.ZERO).
        BigDecimal nuevoTotal = BigDecimal.ZERO;
        for (DetallePedido detalle : pedido.getDetalles()) {
            nuevoTotal = nuevoTotal.add(detalle.getSubtotal());
        }
        System.out.println(nuevoTotal);

        // 2. Actualizar Pedido.total
        pedido.setTotal(nuevoTotal);
        pedidoRepositorio.save(pedido);

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
        // Busca el pedido o lanza excepción
        Pedido pedido = pedidoRepositorio.findById(pedidoId).orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));

        Transaccion transaccion = pedido.getTransaccion();
        BigDecimal monto = pedido.getTotal(); 
        
        // Si el monto no está actualizado, lo actualiza.
        if (transaccion.getMonto().compareTo(monto) != 0) transaccion.setMonto(monto);
        
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
            default -> throw new UnsupportedOperationException("Tipo de Transacción no soportado: " + transaccion.getTipo());
        }

        return true;
    }
}
