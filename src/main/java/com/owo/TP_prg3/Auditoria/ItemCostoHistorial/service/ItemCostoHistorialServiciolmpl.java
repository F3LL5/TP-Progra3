package com.owo.TP_prg3.Auditoria.ItemCostoHistorial.service;

import com.owo.TP_prg3.Auditoria.ItemCostoHistorial.dto.ItemCostoHistorialDTO;
import com.owo.TP_prg3.Auditoria.ItemCostoHistorial.modelo.ItemCostoHistorial;
import com.owo.TP_prg3.Auditoria.ItemCostoHistorial.modelo.ItemCostoHistorialRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemCostoHistorialServiciolmpl implements ItemCostoHistorialServicio {

    /// ATRIBUTOS
    @Autowired
    private ItemCostoHistorialRepositorio itemCostoHistorialRepositorio;

    /// CONVERSION
    private ItemCostoHistorialDTO convertirADto(ItemCostoHistorial itemCostoHistorial){
        return new ItemCostoHistorialDTO(
                itemCostoHistorial.getId_historial_item(),
                itemCostoHistorial.getItem_id(),
                itemCostoHistorial.getCosto_anterior(),
                itemCostoHistorial.getCosto_nuevo(),
                itemCostoHistorial.getFecha_cambio()
        );
    }

    /// METODOS
    @Override
    public List<ItemCostoHistorialDTO> getAll() {
        return itemCostoHistorialRepositorio.findAll()
                .stream()
                .map(this::convertirADto)
                .toList();
    }

    @Override
    public Optional<ItemCostoHistorialDTO> buscarPorID(Long id) {
        return itemCostoHistorialRepositorio.findById(id).map(this::convertirADto);
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
