package com.owo.TP_prg3.Clases.Transaccion.service;

import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Transaccion.dto.FormTransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TransaccionRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransaccionServicio implements I_CRUD<Transaccion, TransaccionDTO, FormTransaccionDTO> {

    // ATRIBUTOS ------------------------------------------------------------------------------------------------------------------------------------------------
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;

    // CONVERSION ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public Transaccion convertir_a_Obj(FormTransaccionDTO fDTO) {
        Transaccion t = new Transaccion();
        t.setTransaccion_id(null);
        t.setTipo(fDTO.getTipo());
        // Si no se proporciona fecha, usa la actual
        t.setFecha(fDTO.getFecha() != null ? fDTO.getFecha() : LocalDateTime.now());
        t.setMonto(fDTO.getMonto());
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

    // ESCRITURA (POST) ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean cargar(FormTransaccionDTO cDTO) {
        transaccionRepositorio.save(convertir_a_Obj(cDTO));
        return true;
    }

    // ACTUALIZACION (PUT) ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    @Transactional
    public boolean actualizar(Long id, FormTransaccionDTO updateDTO) {
        Optional<Transaccion> optional = transaccionRepositorio.findById(id);
        if (optional.isEmpty()) return false;

        Transaccion transaccion = optional.get();
        transaccion.setTipo(updateDTO.getTipo());
        transaccion.setMonto(updateDTO.getMonto());
        transaccion.setFecha(updateDTO.getFecha() != null ? updateDTO.getFecha() : transaccion.getFecha());
        transaccion.setOrigen_id(updateDTO.getOrigen_id());
        transaccion.setDestino_id(updateDTO.getDestino_id());

        transaccionRepositorio.save(transaccion);
        return true;
    }

    // ELIMINACION (DELETE) ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean eliminar(Long id) {
        Optional<Transaccion> optional = transaccionRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        transaccionRepositorio.delete(optional.get());
        return true;
    }
}
