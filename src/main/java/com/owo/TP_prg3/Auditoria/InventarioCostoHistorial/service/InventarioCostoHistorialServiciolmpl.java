package com.owo.TP_prg3.Auditoria.InventarioCostoHistorial.service;

import com.owo.TP_prg3.Auditoria.InventarioCostoHistorial.dto.InventarioCostoHistorialDTO;
import com.owo.TP_prg3.Auditoria.InventarioCostoHistorial.modelo.InventarioCostoHistorial;
import com.owo.TP_prg3.Auditoria.InventarioCostoHistorial.modelo.InventarioCostoHistorialRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioCostoHistorialServiciolmpl implements InventarioCostoHistorialServicio {

    /// ATRIBUTOS
    @Autowired
    private InventarioCostoHistorialRepositorio inventarioCostoHistorialRepositorio;

    /// CONVERSION
    private InventarioCostoHistorialDTO convertirADto(InventarioCostoHistorial inventarioCostoHistorial){
        return new InventarioCostoHistorialDTO(
                inventarioCostoHistorial.getId_historial_item(),
                inventarioCostoHistorial.getItem_id(),
                inventarioCostoHistorial.getCosto_anterior(),
                inventarioCostoHistorial.getCosto_nuevo(),
                inventarioCostoHistorial.getFecha_cambio()
        );
    }

    /// METODOS
    @Override
    public List<InventarioCostoHistorialDTO> getAll() {
        return inventarioCostoHistorialRepositorio.findAll()
                .stream()
                .map(this::convertirADto)
                .toList();
    }

    @Override
    public Optional<InventarioCostoHistorialDTO> buscarPorID(Long id) {
        return inventarioCostoHistorialRepositorio.findById(id).map(this::convertirADto);
    }

    @Override
    public String listado() {
        StringBuilder s = new StringBuilder();
        getAll().forEach(i -> s
                .append( i.getId() + ". ")
                .append( i )
                .append(",\n"));
        return s.toString();
    }


}
