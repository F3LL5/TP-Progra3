package com.owo.TP_prg3.Auditoria.DuenioHistorial.service;

import com.owo.TP_prg3.Auditoria.DuenioHistorial.dto.DuenioHistorialDTO;
import com.owo.TP_prg3.Auditoria.DuenioHistorial.modelo.DuenioHistorial;
import com.owo.TP_prg3.Auditoria.DuenioHistorial.modelo.DuenioHistorialRepositorio;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class DuenioHistorialServiciolmpl implements DuenioHistorialServicio{
    /// ATRIBUTOS
    @Autowired
    private DuenioHistorialRepositorio duenioHistorialRepositorio;

    /// CONVERSION
    private DuenioHistorialDTO convertirADto (DuenioHistorial duenioHistorial){
        return new DuenioHistorialDTO(
                duenioHistorial.getId_historial_duenio(),
                duenioHistorial.getPuesto_id(),
                duenioHistorial.getDuenio_anterior_id(),
                duenioHistorial.getDuenio_nuevo_id(),
                duenioHistorial.getFecha_cambio()
        );
    }

    /// METODOS
    @Override
    public List<DuenioHistorialDTO> getAll() {
        return duenioHistorialRepositorio.findAll()
                .stream()
                .map(this::convertirADto)
                .toList();
    }

    @Override
    public Optional<DuenioHistorialDTO> buscarPorId(Long id) {
        return duenioHistorialRepositorio.findById(id).map(this::convertirADto);
    }

    @Override
    public String listado() {
        StringBuilder s = new StringBuilder();
        getAll().forEach(i -> s
                .append( i.getId_historial_nuevo() + ". ")
                .append( i )
                .append(",\n"));
        return s.toString();;
    }




}
