package com.owo.TP_prg3.Clases.Transaccion.service;

import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancaria;
import com.owo.TP_prg3.Clases.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Clases.Excepciones.RecursoNoEncontradoException;
import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Pedido.modelo.PedidoRepositorio;
import com.owo.TP_prg3.Clases.Puesto.modelo.Puesto;
import com.owo.TP_prg3.Clases.Puesto.modelo.PuestoRepositorio;
import com.owo.TP_prg3.Clases.Transaccion.dto.CreateTransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.UpdateTransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TipoTransaccion;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TransaccionRepositorio;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancariaRepositorio; // Para inyectar el repositorio de CuentaBancaria
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar para @Transactional
import jakarta.persistence.EntityNotFoundException; // Para manejar casos donde no se encuentra una entidad referenciada

import java.math.BigDecimal;
import java.time.LocalDateTime; // Para manejar el campo fecha
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransaccionServicioImpl implements TransaccionServicio {

    @Autowired
    private TransaccionRepositorio transaccionRepositorio;
    @Autowired
    private CuentaBancariaRepositorio cuentaBancariaRepositorio;
    @Autowired
    private PuestoRepositorio puestoRepositorio;
    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    //Conversióm
    private TransaccionDTO convertirA_DTO(Transaccion transaccion) {
        return new TransaccionDTO(
                transaccion.getTransaccionId(),
                transaccion.getTipo().name(),
                transaccion.getFecha().toString(),
                transaccion.getMonto(),
                transaccion.getCuentaOrigen() != null ? transaccion.getCuentaOrigen().getCuentaBancariaId() : null,
                transaccion.getCuentaDestino() != null ? transaccion.getCuentaDestino().getCuentaBancariaId() : null
        );
    }

    private Transaccion convertirA_Transaccion(CreateTransaccionDTO transaccionDTO) {
        Transaccion transaccion = new Transaccion();
        transaccion.setTipo(TipoTransaccion.valueOf(transaccionDTO.getTipo()));
        transaccion.setFecha(LocalDateTime.now()); // La fecha se establece al momento de la creación en el servicio

        // Cargar las entidades de CuentaBancaria si los IDs son proporcionados
        if (transaccionDTO.getCuentaOrigenId() != null) {
            cuentaBancariaRepositorio.findById(transaccionDTO.getCuentaOrigenId())
                    .ifPresentOrElse(
                            transaccion::setCuentaOrigen,
                            () -> { throw new EntityNotFoundException("Cuenta de origen con ID " + transaccionDTO.getCuentaOrigenId() + " no encontrada."); }
                    );
        }

        if (transaccionDTO.getCuentaDestinoId() != null) {
            cuentaBancariaRepositorio.findById(transaccionDTO.getCuentaDestinoId())
                    .ifPresentOrElse(
                            transaccion::setCuentaDestino,
                            () -> { throw new EntityNotFoundException("Cuenta de destino con ID " + transaccionDTO.getCuentaDestinoId() + " no encontrada."); }
                    );
        }

        transaccion.setMonto(transaccionDTO.getMonto());
        return transaccion;
    }

    /// GET ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public List<TransaccionDTO> getAllTransacciones() {
        return transaccionRepositorio.findAll()
                .stream()
                .map(this::convertirA_DTO)
                .toList();
    }

    @Override
    public Optional<TransaccionDTO> getTransaccionById(Long id) {
        return transaccionRepositorio.findById(id).map(this::convertirA_DTO);
    }

    @Override
    public String listado(){
        StringBuilder s = new StringBuilder();
        getAllTransacciones().forEach(t-> s
                .append( t.getTransaccionId() + ". " )
                .append( t )
                .append(",\n"));
        return s.toString();
    }

    public List<TransaccionDTO> getTransaccionesByPuestoId(Long puestoId) {
        puestoRepositorio.findById(puestoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Puesto con ID " + puestoId + " no encontrado."));

        // Obtener todos los pedidos asociados a este puesto
        List<Pedido> pedidosDelPuesto = pedidoRepositorio.findAll().stream()
                .filter(pedido -> pedido.getPuestoId().equals(puestoId))
                .toList();

        // Recopilar IDs de transacciones de estos pedidos
        List<Long> transaccionIdsAsociadas = pedidosDelPuesto.stream()
                .map(pedido -> pedido.getTransaccion().getTransaccionId())
                .distinct()
                .toList();

        // Filtrar transacciones que coincidan con los IDs recopilados
        return transaccionRepositorio.findAll().stream()
                .filter(transaccion -> transaccionIdsAsociadas.contains(transaccion.getTransaccionId()))
                .map(this::convertirA_DTO)
                .collect(Collectors.toList());
    }

    public Optional<TransaccionDTO> getTransaccionByIdAndPuestoId(Long id, Long puestoId) {
        return getTransaccionById(id).filter(transaccionDTO ->
                getTransaccionesByPuestoId(puestoId).stream()
                        .anyMatch(t -> t.getTransaccionId().equals(transaccionDTO.getTransaccionId()))
        );
    }

    public List<TransaccionDTO> getTransaccionesByCuentaBancariaId(Long cuentaBancariaId) {
        CuentaBancaria cuenta = cuentaBancariaRepositorio.findById(cuentaBancariaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta bancaria con ID " + cuentaBancariaId + " no encontrada."));

        return transaccionRepositorio.findAll().stream()
                .filter(transaccion ->
                        (transaccion.getCuentaOrigen() != null && transaccion.getCuentaOrigen().getCuentaBancariaId().equals(cuentaBancariaId)) ||
                                (transaccion.getCuentaDestino() != null && transaccion.getCuentaDestino().getCuentaBancariaId().equals(cuentaBancariaId))
                )
                .map(this::convertirA_DTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransaccionDTO> filtrarYOrdenar(String tipo_transaccion, String sortBy, String sortDir) {
        List<TransaccionDTO> allTransacciones = getAllTransacciones();
        Stream<TransaccionDTO> transaccionDTOStream = allTransacciones.stream();

        if(tipo_transaccion != null && !tipo_transaccion.isEmpty()) {
            transaccionDTOStream = transaccionDTOStream.filter(transaccion -> transaccion.getTipo().equalsIgnoreCase(tipo_transaccion));
        }

        if(sortBy != null) {
            Comparator<TransaccionDTO> comparator = null;
            switch (sortBy.toLowerCase()) {
                case "fecha" -> comparator = Comparator.comparing(TransaccionDTO::getFecha);
                case "monto" -> comparator = Comparator.comparing(TransaccionDTO::getMonto);
                case "id_cuenta_origen" -> comparator = Comparator.comparing(TransaccionDTO::getCuentaOrigenId);
                case "id_cuenta_destino" -> comparator = Comparator.comparing(TransaccionDTO::getCuentaDestinoId);
                default -> throw new RuntimeException("Dicho criterio de búsqueda NO existe.");
            }
            if(comparator != null) if("desc".equalsIgnoreCase(sortDir)) comparator = comparator.reversed();

            transaccionDTOStream = transaccionDTOStream.sorted(comparator);
        }

        return transaccionDTOStream.toList();
    }

    public List<TransaccionDTO> filtrarYOrdenarTransaccionesByPuestoId(Long puestoId, String tipo_transaccion, String sortBy, String sortDir) {
        Stream<TransaccionDTO> transaccionDTOStream = getTransaccionesByPuestoId(puestoId).stream();

        if (tipo_transaccion != null && !tipo_transaccion.isBlank()) {
            try {
                TipoTransaccion tipo = TipoTransaccion.valueOf(tipo_transaccion.toUpperCase());
                transaccionDTOStream = transaccionDTOStream.filter(t -> t.getTipo().equals(tipo_transaccion));
            } catch (IllegalArgumentException e) {
                throw new IngresoInvalidoException("Tipo de transacción inválido: " + tipo_transaccion);
            }
        }

        if (sortDir != null && !sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            throw new IngresoInvalidoException("La dirección de ordenamiento debe ser 'asc' o 'desc'.");
        }

        if (sortBy != null) {
            Comparator<TransaccionDTO> comparator = null;
            switch (sortBy.toLowerCase()) {
                case "fecha" -> comparator = Comparator.comparing(TransaccionDTO::getFecha);
                case "monto" -> comparator = Comparator.comparing(TransaccionDTO::getMonto);
                case "id_cuenta_origen" -> comparator = Comparator.comparing(t -> Optional.ofNullable(t.getCuentaOrigenId()).orElse(0L));
                case "id_cuenta_destino" -> comparator = Comparator.comparing(t -> Optional.ofNullable(t.getCuentaDestinoId()).orElse(0L));
                default -> throw new IngresoInvalidoException("Criterio de ordenamiento no válido: " + sortBy);
            }
            if (comparator != null) {
                if ("desc".equalsIgnoreCase(sortDir)) {
                    comparator = comparator.reversed();
                }
                transaccionDTOStream = transaccionDTOStream.sorted(comparator);
            }
        }
        return transaccionDTOStream.collect(Collectors.toList());
    }

    public List<TransaccionDTO> filtrarYOrdenarTransaccionesByCuentaBancariaId(
            Long cuentaBancariaId,
            String tipo_transaccion,
            String sortBy,
            String sortDir
    ) {
        Stream<TransaccionDTO> transaccionDTOStream = getTransaccionesByCuentaBancariaId(cuentaBancariaId).stream();

        if (tipo_transaccion != null && !tipo_transaccion.isBlank()) {
            try {
                TipoTransaccion tipo = TipoTransaccion.valueOf(tipo_transaccion.toUpperCase());
                transaccionDTOStream = transaccionDTOStream.filter(t -> t.getTipo().equals(tipo_transaccion));
            } catch (IllegalArgumentException e) {
                throw new IngresoInvalidoException("Tipo de transacción inválido: " + tipo_transaccion);
            }
        }

        if (sortDir != null && !sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            throw new IngresoInvalidoException("La dirección de ordenamiento debe ser 'asc' o 'desc'.");
        }

        if (sortBy != null) {
            Comparator<TransaccionDTO> comparator = null;
            switch (sortBy.toLowerCase()) {
                case "fecha" -> comparator = Comparator.comparing(TransaccionDTO::getFecha);
                case "monto" -> comparator = Comparator.comparing(TransaccionDTO::getMonto);
                case "id_cuenta_origen" -> comparator = Comparator.comparing(t -> Optional.ofNullable(t.getCuentaOrigenId()).orElse(0L));
                case "id_cuenta_destino" -> comparator = Comparator.comparing(t -> Optional.ofNullable(t.getCuentaDestinoId()).orElse(0L));
                default -> throw new IngresoInvalidoException("Criterio de ordenamiento no válido: " + sortBy);
            }
            if (comparator != null) {
                if ("desc".equalsIgnoreCase(sortDir)) {
                    comparator = comparator.reversed();
                }
                transaccionDTOStream = transaccionDTOStream.sorted(comparator);
            }
        }
        return transaccionDTOStream.collect(Collectors.toList());
    }

    /// POST ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public TransaccionDTO createTransaccion(CreateTransaccionDTO createTransaccionDTO) {
        Transaccion transaccion = convertirA_Transaccion(createTransaccionDTO);

        CuentaBancaria cuentaDestino = transaccion.getCuentaDestino();
        CuentaBancaria cuentaOrigen = transaccion.getCuentaOrigen();

        switch (transaccion.getTipo()){
            case TipoTransaccion.VENTA -> {
                if (cuentaDestino.getSaldo().compareTo(transaccion.getMonto())<0) throw new IllegalArgumentException("Saldo insuficiente en la cuenta de origen para esta transacción.");


                cuentaOrigen.setSaldo( cuentaOrigen.getSaldo().add(transaccion.getMonto()) );

                cuentaDestino.setSaldo( cuentaDestino.getSaldo().subtract(transaccion.getMonto()) );

                cuentaBancariaRepositorio.save(cuentaDestino);
                cuentaBancariaRepositorio.save(cuentaOrigen);
            }
            case TipoTransaccion.COMPRA -> {
                if (cuentaOrigen.getSaldo().compareTo(transaccion.getMonto())<0) throw new IllegalArgumentException("Saldo insuficiente en la cuenta de origen para esta transacción.");


                cuentaOrigen.setSaldo( cuentaOrigen.getSaldo().subtract(transaccion.getMonto()) );

                cuentaDestino.setSaldo( cuentaDestino.getSaldo().add(transaccion.getMonto()) );

                cuentaBancariaRepositorio.save(cuentaDestino);
                cuentaBancariaRepositorio.save(cuentaOrigen);
            }
            case TipoTransaccion.INGRESO -> {

                cuentaDestino.setSaldo( cuentaDestino.getSaldo().add(transaccion.getMonto()) );
                cuentaBancariaRepositorio.save(cuentaDestino);
            }
            case TipoTransaccion.EGRESO -> {

                cuentaDestino.setSaldo( cuentaDestino.getSaldo().subtract(transaccion.getMonto()) );
                cuentaBancariaRepositorio.save(cuentaDestino);
            }
        }
        Transaccion savedTransaccion = transaccionRepositorio.save(transaccion);
        return convertirA_DTO(savedTransaccion);
    }

    /// DELETE ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteTransaccion(Long id) {
        if (transaccionRepositorio.existsById(id)) {
            Transaccion transaccion = transaccionRepositorio.findById(id).get();

            revertirMovimientoDeFondos(transaccion);
            transaccionRepositorio.deleteById(id);
            return true;
        }
        return false;
    }

    /// PATCH ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional // Aplica la gestión transaccional
    public Optional<TransaccionDTO> updateTransaccion(Long id, UpdateTransaccionDTO updateTransaccionDTO) {
        return transaccionRepositorio.findById(id)
                .map(transaccion -> {
                    if (updateTransaccionDTO.getTipo() != null) {
                        transaccion.setTipo(TipoTransaccion.valueOf(updateTransaccionDTO.getTipo()));
                    }
                    if (updateTransaccionDTO.getFecha() != null) {
                        transaccion.setFecha(updateTransaccionDTO.getFecha());
                    }
                    if (updateTransaccionDTO.getMonto() != null) {
                        transaccion.setMonto(updateTransaccionDTO.getMonto());
                    }

                    if (updateTransaccionDTO.getCuentaOrigenId() != null) {
                        cuentaBancariaRepositorio.findById(updateTransaccionDTO.getCuentaOrigenId())
                                .ifPresentOrElse(
                                        transaccion::setCuentaOrigen,
                                        () -> { throw new EntityNotFoundException("Cuenta de origen con ID " + updateTransaccionDTO.getCuentaOrigenId() + " no encontrada."); }
                                );
                    }

                    if (updateTransaccionDTO.getCuentaDestinoId() != null) {
                        cuentaBancariaRepositorio.findById(updateTransaccionDTO.getCuentaDestinoId())
                                .ifPresentOrElse(
                                        transaccion::setCuentaDestino,
                                        () -> { throw new EntityNotFoundException("Cuenta de destino con ID " + updateTransaccionDTO.getCuentaDestinoId() + " no encontrada."); }
                                );
                    }

                    Transaccion updatedTransaccion = transaccionRepositorio.save(transaccion);
                    return convertirA_DTO(updatedTransaccion);
                });
    }

    @Transactional
    public void ajustarSaldosPorCambioDeMonto(Long transaccionId, BigDecimal previousMonto, BigDecimal newMonto) {
        Transaccion transaccion = transaccionRepositorio.findById(transaccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transaccion con ID " + transaccionId + " no encontrada para ajustar saldos."));

        if (previousMonto == null) previousMonto = BigDecimal.ZERO;
        if (newMonto == null) newMonto = BigDecimal.ZERO;

        BigDecimal montoDelta = newMonto.subtract(previousMonto);

        if (montoDelta.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        CuentaBancaria cuentaDestino = transaccion.getCuentaDestino();
        CuentaBancaria cuentaOrigen = transaccion.getCuentaOrigen();
        TipoTransaccion tipo = transaccion.getTipo();

        switch (tipo) {
            case VENTA:
                if (cuentaDestino == null) throw new IllegalArgumentException("Cuenta de destino requerida para VENTA.");
                if (cuentaOrigen == null) throw new IllegalArgumentException("Cuenta de origen requerida para VENTA.");

                cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().add(montoDelta));
                cuentaDestino.setSaldo(cuentaDestino.getSaldo().subtract(montoDelta));
                break;
            case COMPRA:
                if (cuentaOrigen == null) throw new IllegalArgumentException("Cuenta de origen requerida para COMPRA.");
                if (cuentaDestino == null) throw new IllegalArgumentException("Cuenta de destino requerida para COMPRA.");

                cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(montoDelta));
                cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(montoDelta));
                break;
            case INGRESO:
                if (cuentaDestino == null) throw new IllegalArgumentException("Cuenta de destino requerida para INGRESO.");
                cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(montoDelta));
                break;
            case EGRESO:
                if (cuentaDestino == null) throw new IllegalArgumentException("Cuenta de destino requerida para EGRESO.");
                cuentaDestino.setSaldo(cuentaDestino.getSaldo().subtract(montoDelta));
                break;
            default:
                throw new IllegalArgumentException("Tipo de transacción no soportado para el ajuste de saldos: " + tipo);
        }

        if (cuentaOrigen != null) {
            cuentaBancariaRepositorio.save(cuentaOrigen);
        }
        if (cuentaDestino != null && !cuentaDestino.equals(cuentaOrigen)) {
            cuentaBancariaRepositorio.save(cuentaDestino);
        }
    }

    @Transactional
    private void revertirMovimientoDeFondos(Transaccion transaccion) {

        CuentaBancaria cuentaDestino = transaccion.getCuentaDestino();
        CuentaBancaria cuentaOrigen = transaccion.getCuentaOrigen();
        BigDecimal monto = transaccion.getMonto();

        if (monto == null || monto.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        switch (transaccion.getTipo()) {
            case VENTA:
                if (cuentaDestino == null || cuentaOrigen == null) {  return; }
                cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(monto));
                cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(monto));
                break;
            case COMPRA:
                if (cuentaOrigen == null || cuentaDestino == null) {  return; }
                cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().add(monto));
                cuentaDestino.setSaldo(cuentaDestino.getSaldo().subtract(monto));
                break;
            case INGRESO:
                if (cuentaDestino == null) { return; }
                cuentaDestino.setSaldo(cuentaDestino.getSaldo().subtract(monto));
                break;
            case EGRESO:
                if (cuentaDestino == null) {  return; }
                cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(monto));
                break;
            default:
                System.err.println("Advertencia: Tipo de transacción no soportado para la reversión de fondos: " + transaccion.getTipo());
                break;
        }

        if (cuentaOrigen != null) {
            cuentaBancariaRepositorio.save(cuentaOrigen);
        }
        if (cuentaDestino != null && !cuentaDestino.equals(cuentaOrigen)) {
            cuentaBancariaRepositorio.save(cuentaDestino);
        }
    }

























































}
