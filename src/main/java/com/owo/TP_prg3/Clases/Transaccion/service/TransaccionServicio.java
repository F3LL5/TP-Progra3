package com.owo.TP_prg3.Clases.Transaccion.service;

import com.owo.TP_prg3.Clases.Herramientas.ExcelExportService;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Transaccion.dto.FormTransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TransaccionRepositorio;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.ValidacionGeneral;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransaccionServicio implements I_CRUD<Transaccion, TransaccionDTO, FormTransaccionDTO> {

    // ATRIBUTOS ------------------------------------------------------------------------------------------------------------------------------------------------
    private static final String ENTIDAD = "Transaccion";
    
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;

     
    @Autowired
    private ExcelExportService excelExportService;

    // CONVERSION ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public Transaccion convertir_a_Obj(FormTransaccionDTO fDTO) {
        validarDatosTransaccion(fDTO);
        Transaccion t = new Transaccion();

        t.setTipo(fDTO.getTipo());
        
        // 1. Inicialización de campos obligatorios (fecha) con valores seguros
        t.setFecha(fDTO.getFecha() != null ? fDTO.getFecha() : LocalDateTime.now());
        t.setMonto(BigDecimal.ZERO);

        t.setOrigen_id(fDTO.getOrigen_id()); 
        t.setDestino_id(fDTO.getDestino_id()); 
        
        return t;
    }

    @Override
    public TransaccionDTO convertir_a_DTO(Transaccion transaccion) {
        return new TransaccionDTO(
            transaccion.getTransaccion_id(),
            transaccion.getTipo(),
            transaccion.getFecha(),
            transaccion.getMonto(),
            transaccion.getOrigen_id(),
            transaccion.getDestino_id()
        );
    }

    // LECTURA (GET) ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public Set<TransaccionDTO> obtenerTodos() {
        return transaccionRepositorio.findAll().stream()
            .map(this::convertir_a_DTO)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<TransaccionDTO> buscarPorID(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return transaccionRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    @Override
    public Set<TransaccionDTO> filtrar(String campo, Object valor) {
        Stream<Transaccion> stream = transaccionRepositorio.findAll().stream();

        Predicate<Transaccion> filtro = switch (campo.toLowerCase()) {
            case "tipo" -> p -> p.getTipo().toString().equalsIgnoreCase(String.valueOf(valor));
            case "origenid" -> p -> p.getOrigen_id() != null && p.getOrigen_id().equals(Long.valueOf(String.valueOf(valor)));
            case "destinoid" -> p -> p.getDestino_id().equals(Long.valueOf(String.valueOf(valor)));
            default -> p -> false;
        };

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    @Override
    public Set<TransaccionDTO> ordenar(String campo, boolean ascendente) {
        Stream<Transaccion> stream = transaccionRepositorio.findAll().stream();
        Comparator<Transaccion> comparador;

        comparador = switch (campo.toLowerCase()) {
            case "monto" -> Comparator.comparing(Transaccion::getMonto);
            case "fecha" -> Comparator.comparing(Transaccion::getFecha);
            case "tipo" -> Comparator.comparing(Transaccion::getTipo);
            default -> Comparator.comparing(Transaccion::getTransaccion_id);
        };
        
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public byte[] exportar() {
        List<TransaccionDTO> lista = transaccionRepositorio.findAll().stream()
            .map(this::convertir_a_DTO)
            .sorted(Comparator.comparing(TransaccionDTO::getTransaccion_id))
            .collect(Collectors.toList());
        byte[] excel = excelExportService.generarExcel(lista,"Transacciones");
        return excel;
    }



    // ESCRITURA (POST) ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean cargar(FormTransaccionDTO cDTO) {
        validarDatosTransaccion(cDTO);
        transaccionRepositorio.save(convertir_a_Obj(cDTO));
        return true;
    }

    // ACTUALIZACION (PUT) ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    @Transactional
    public boolean actualizar(Long id, FormTransaccionDTO updateDTO) {
        validarDatosTransaccion(updateDTO);
        Transaccion transaccion = obtenerTransaccionPorId(id);

        if (updateDTO.getTipo() != null) transaccion.setTipo(updateDTO.getTipo());
        if (updateDTO.getFecha() != null) transaccion.setFecha(updateDTO.getFecha());
        if (updateDTO.getOrigen_id() != null) transaccion.setOrigen_id(updateDTO.getOrigen_id());
        if (updateDTO.getDestino_id() != null) transaccion.setDestino_id(updateDTO.getDestino_id());

        transaccionRepositorio.save(transaccion);
        return true;
    }

    // ELIMINACION (DELETE) ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean eliminar(Long id) {
        Transaccion transaccion = obtenerTransaccionPorId(id);
        transaccionRepositorio.delete(transaccion);
        return true;
    }

    // VALIDACIONES PRIVADAS
    private void validarDatosTransaccion(FormTransaccionDTO dto) {
        if (dto == null) throw new IngresoInvalidoException("Los datos de " + ENTIDAD + " no pueden ser nulos");

        if (dto.getTipo() == null) throw new CampoRequeridoException("tipo");
        
        if (dto.getOrigen_id() != null) ValidacionGeneral.validarIdValido(dto.getOrigen_id());
        if (dto.getDestino_id() != null) ValidacionGeneral.validarIdValido(dto.getDestino_id());

        // La fecha no se valida aquí, ya que el sistema puede establecerla automáticamente.
    }

    private Transaccion obtenerTransaccionPorId(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return transaccionRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }
}
