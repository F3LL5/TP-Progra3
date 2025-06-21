package com.owo.TP_prg3.Clases.Pedido.service;

import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancaria;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancariaRepositorio;
import com.owo.TP_prg3.Clases.DetallePedido.service.DetallePedidoServicioImpl;
import com.owo.TP_prg3.Clases.Puesto.modelo.PuestoRepositorio;
import com.owo.TP_prg3.Excepciones.RecursoNoEncontradoException;
import com.owo.TP_prg3.Clases.Pedido.dto.CreatePedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.CreatePedidoDTO2;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.UpdatePedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Pedido.modelo.PedidoRepositorio;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TipoTransaccion;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TransaccionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PedidoServicioImpl implements PedidoServicio {
    //ATRIBUTOS
    @Autowired
    private PedidoRepositorio pedidoRepositorio;
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;
    @Autowired
    private CuentaBancariaRepositorio cuentaBancariaRepositorio;
    @Autowired
    private PuestoRepositorio puestoRepositorio;

    @Autowired
    private DetallePedidoServicioImpl detallePedidoServicio;

    //CONVERSION
    private PedidoDTO convertirA_DTO(Pedido pedido) {
        return new PedidoDTO(
                pedido.getPedidoId(),
                pedido.getTransaccion().getTransaccionId(),
                pedido.getPuestoId()
        );
    }

    private Pedido convertirA_Pedido(CreatePedidoDTO createPedidoDTO) {
        Pedido pedido = new Pedido();

        transaccionRepositorio.findById(createPedidoDTO.getTransaccionId())
                .ifPresentOrElse(
                        pedido::setTransaccion,
                        () -> {
                            throw new RecursoNoEncontradoException("Transacción con ID " + createPedidoDTO.getTransaccionId() + " no encontrada.");
                        }
                );

        Long puestoId = createPedidoDTO.getPuestoId();
        if (!puestoRepositorio.existsById(puestoId))
            throw new RecursoNoEncontradoException("Puesto con ID " + puestoId + " no encontrado.");

        pedido.setPuestoId(puestoId);

        return pedido;
    }

    //METODOS
    @Override
    public List<PedidoDTO> getAllPedidos() {
        return pedidoRepositorio.findAll().stream()
                .map(this::convertirA_DTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PedidoDTO> getPedidoById(Long id) {
        return Optional.of(pedidoRepositorio.findById(id)
                .map(this::convertirA_DTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido con ID " + id + " no encontrado.")));
    }

    @Override
    @Transactional
    public PedidoDTO createPedido(CreatePedidoDTO createPedidoDTO) {
        Pedido pedido = convertirA_Pedido(createPedidoDTO);
        Pedido savedPedido = pedidoRepositorio.save(pedido);
        return convertirA_DTO(savedPedido);
    }

    @Transactional
    public PedidoDTO createPedidoYtransaccion(CreatePedidoDTO2 createPedidoDTO2) {
        Transaccion transaccion = new Transaccion();
        transaccion.setTipo(TipoTransaccion.valueOf(createPedidoDTO2.getTipoTransaccion()));
        transaccion.setMonto(BigDecimal.ZERO);

        CuentaBancaria cuentaDuenio=cuentaBancariaRepositorio.findAll().stream()
                .filter(cuentaBancaria -> cuentaBancaria.getEntidad().getEntidad_id().equals(createPedidoDTO2.getIdEntidad()))
                .findFirst()
                .orElseThrow();

        if(cuentaBancariaRepositorio.findById(createPedidoDTO2.getIdCuentaDestino()).isEmpty() && createPedidoDTO2.getIdCuentaDestino()!= 0) {
            throw new RecursoNoEncontradoException("Transaccion con ID " + createPedidoDTO2.getIdCuentaDestino() + " no encontrada.");
        }

        transaccion.setCuentaOrigen(cuentaDuenio);
        if (createPedidoDTO2.getIdCuentaDestino() == 0)transaccion.setCuentaDestino(new CuentaBancaria());
        else transaccion.setCuentaDestino(cuentaBancariaRepositorio.findById(createPedidoDTO2.getIdCuentaDestino()).get());
        transaccion.setFecha(LocalDateTime.now());
        Transaccion transaccion1=transaccionRepositorio.save(transaccion);

        Pedido pedido=new Pedido();
        pedido.setTransaccion(transaccion1);
        pedido.setPuestoId(createPedidoDTO2.getPuestoId());

        Pedido savedPedido = pedidoRepositorio.save(pedido);

        return convertirA_DTO(savedPedido);
    }

    @Override
    @Transactional
    public Optional<PedidoDTO> updatePedido(Long id, UpdatePedidoDTO updatePedidoDTO) {
        return pedidoRepositorio.findById(id)
                .map(pedido -> {
                    if (updatePedidoDTO.getTransaccionId() != null) {
                        transaccionRepositorio
                                .findById(updatePedidoDTO.getTransaccionId())
                                .ifPresentOrElse( pedido::setTransaccion, () -> {
                                    throw new RecursoNoEncontradoException("Transaccion con ID " + updatePedidoDTO.getTransaccionId() + " no encontrada.");
                                });
                    }
                    if (updatePedidoDTO.getPuestoId() != null) {
                        pedido.setPuestoId(updatePedidoDTO.getPuestoId());
                    }

                    Pedido updatedPedido = pedidoRepositorio.save(pedido);
                    return convertirA_DTO(updatedPedido);
                }).or(() -> {throw new RecursoNoEncontradoException("Pedido con ID " + id + " no encontrado.");
                });
    }

    @Override
    public boolean deletePedido(Long id) {
        if (!pedidoRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoException("Pedido con ID " + id + " no encontrado para eliminar.");
        }
        pedidoRepositorio.deleteById(id);
        return true;
    }

    @Override
    public List<PedidoDTO> filtrarYOrdenar(String tipo_transaccion, LocalDate fechaMin, LocalDate fechaMax, String sortBy, String sortDir) {
        List<PedidoDTO> pedidos = getAllPedidos();
        Stream<PedidoDTO> pedidoDTOStream = pedidos.stream();

        /// FILTRADO POR TIPO DE TRANSACCION
        if (tipo_transaccion != null && !tipo_transaccion.isEmpty()) {
            pedidoDTOStream = pedidoDTOStream.filter(pedido -> transaccionRepositorio.getById(pedido.getTransaccionId()).getTipo().name().equalsIgnoreCase(tipo_transaccion) );
        }

        /// FILTRADO DE FECHAS
        if ( fechaMin!=null  &&  fechaMax!=null  &&  fechaMin.isAfter(fechaMax) ){ //Si se ingresa un intervalo de fechas y son una contradiccion los da vuelta
            LocalDate aux = fechaMin;
            fechaMin = fechaMax;
            fechaMax = aux;
        }

        if ( fechaMin != null ){
            final LocalDateTime fechaMinDateTime = fechaMin.atStartOfDay();

            // Primero obtiene las transaccioens mayores a la fecha min
            List<Transaccion> transacciones = transaccionRepositorio.findAll();
            List<Transaccion> transaccionesFiltradasFechaMin = transacciones.stream()
                    .filter( transaccion -> !transaccion.getFecha().isBefore(fechaMinDateTime))
                    .toList();
            // Filtra los pedidos que su transaccion haya sido efectuada desp de la fecha min
            pedidoDTOStream = pedidoDTOStream
                    .filter(pedido ->
                            transaccionesFiltradasFechaMin.stream()
                                    .anyMatch( t -> t.getTransaccionId().equals(pedido.getTransaccionId())));
        }
        if ( fechaMax != null ){
            final LocalDateTime fechaMaxDateTime = fechaMax.atTime(LocalTime.MAX);

            // Primero obtiene las transaccioens menores a la fecha max
            List<Transaccion> transacciones = transaccionRepositorio.findAll();
            List<Transaccion> transaccionesFiltradasFechaMax = transacciones.stream()
                    .filter( transaccion -> !transaccion.getFecha().isAfter( fechaMaxDateTime ))
                    .toList();
            // Filtra los pedidos que su transaccion haya sido efectuada antes de la fecha max
            pedidoDTOStream = pedidoDTOStream
                    .filter(pedido ->
                            transaccionesFiltradasFechaMax.stream()
                                    .anyMatch( t -> t.getTransaccionId().equals(pedido.getTransaccionId())));
        }


        ///  ORDENAMIENTO
        if (sortBy != null) {
            Comparator<PedidoDTO> comparator = null;
            switch (sortBy.toLowerCase()) {
                case "fecha" -> comparator = Comparator.comparing((PedidoDTO pedido) -> transaccionRepositorio.getById(pedido.getTransaccionId()).getFecha());
                case "monto" -> comparator = Comparator.comparing((PedidoDTO pedido) -> transaccionRepositorio.getById(pedido.getTransaccionId()).getMonto());
                case "id_cuenta_origen" -> comparator = Comparator.comparing((PedidoDTO pedido) -> transaccionRepositorio.getById(pedido.getTransaccionId()).getCuentaOrigen().getCuentaBancariaId());
                case "id_cuenta_destino" -> comparator = Comparator.comparing((PedidoDTO pedido) -> transaccionRepositorio.getById(pedido.getTransaccionId()).getCuentaDestino().getCuentaBancariaId());
                default -> throw new RuntimeException("Dicho criterio de búsqueda NO existe.");
            }
            if(comparator != null) if("desc".equalsIgnoreCase(sortDir)) comparator = comparator.reversed();

            pedidoDTOStream = pedidoDTOStream.sorted(comparator);
        }

        return pedidoDTOStream.toList();
    }

    // Obtener todos los pedidos asociados a un puesto específico
    public List<PedidoDTO> getPedidosByPuestoId(Long puestoId) {
        return pedidoRepositorio.findAll().stream()
                .filter(pedido -> pedido.getPuestoId().equals(puestoId))
                .map(this::convertirA_DTO)
                .collect(Collectors.toList());
    }

    // Buscar pedido por ID y Puesto ID
    public Optional<PedidoDTO> getPedidoByIdAndPuestoId(Long id, Long puestoId) {
        return pedidoRepositorio.findById(id)
                .filter(pedido -> pedido.getPuestoId().equals(puestoId))
                .map(this::convertirA_DTO);
    }

    // Eliminar Pedido de un Puesto específico
    @Transactional
    public boolean deletePedidoFromPuesto(Long id, Long puestoId) {
        Optional<Pedido> pedidoOptional = pedidoRepositorio.findById(id);
        if (pedidoOptional.isPresent() && pedidoOptional.get().getPuestoId().equals(puestoId)) {
            pedidoRepositorio.deleteById(id);
            return true;
        } else throw new RecursoNoEncontradoException("Pedido con ID " + id + " no encontrado o no pertenece al puesto " + puestoId + " para eliminar.");
    }

    // Modificar Pedido de un Puesto específico
    @Transactional
    public Optional<PedidoDTO> updatePedidoForPuesto(Long id, Long puestoId, UpdatePedidoDTO updatePedidoDTO) {
        return pedidoRepositorio.findById(id)
                .filter(pedido -> pedido.getPuestoId().equals(puestoId))
                .map(pedido -> {
                    if (updatePedidoDTO.getTransaccionId() != null) {
                        transaccionRepositorio
                                .findById(updatePedidoDTO.getTransaccionId())
                                .ifPresentOrElse( pedido::setTransaccion, () -> {
                                    throw new RecursoNoEncontradoException("Transaccion con ID " + updatePedidoDTO.getTransaccionId() + " no encontrada.");
                                });
                    }

                    Pedido updatedPedido = pedidoRepositorio.save(pedido);
                    return convertirA_DTO(updatedPedido);
                })
                .or(() -> {
                    throw new RecursoNoEncontradoException("Pedido con ID " + id + " no encontrado o no pertenece al puesto " + puestoId + " para actualizar.");
                });
    }

}
