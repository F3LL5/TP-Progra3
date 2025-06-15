package com.owo.TP_prg3.Clases.ItemPrecioHistorial.service;

import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Item.dto.UpdateItemDTO;
import com.owo.TP_prg3.Clases.ItemPrecioHistorial.dto.CreateItemPrecioHistorialDTO;
import com.owo.TP_prg3.Clases.ItemPrecioHistorial.dto.ItemPrecioHistorialDTO;
import com.owo.TP_prg3.Clases.ItemPrecioHistorial.dto.UpdateItemPrecioHistorialDTO;
import com.owo.TP_prg3.Clases.ItemPrecioHistorial.modelo.ItemPrecioHistorial;
import com.owo.TP_prg3.Clases.ItemPrecioHistorial.modelo.ItemPrecioHistorialRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ItemPrecioHistorialServiciolmpl implements ItemPrecioHistorialServicio{
    @Autowired
    private ItemPrecioHistorialRepositorio itemPrecioHistorialRepositorio;

    private ItemPrecioHistorialDTO convertirADto(ItemPrecioHistorial itemPrecioHistorial){
        return new ItemPrecioHistorialDTO(
                itemPrecioHistorial.getId_historial_item(),
                itemPrecioHistorial.getItem_id(),
                itemPrecioHistorial.getPrecio_anterior(),
                itemPrecioHistorial.getPrecio_nuevo(),
                itemPrecioHistorial.getFecha_cambio()
        );
    }

    private ItemPrecioHistorial convertirAItemPrecioHistorial(CreateItemPrecioHistorialDTO createItemPrecioHistorialDTO){
        ItemPrecioHistorial itemPrecioHistorial=new ItemPrecioHistorial();
        itemPrecioHistorial.setId_historial_item(createItemPrecioHistorialDTO.getItem_id());
        itemPrecioHistorial.setPrecio_anterior(createItemPrecioHistorialDTO.getPrecio_anterior());
        itemPrecioHistorial.setPrecio_nuevo(createItemPrecioHistorialDTO.getPrecio_nuevo());
        itemPrecioHistorial.setFecha_cambio(createItemPrecioHistorialDTO.getFecha_cambio());
        return itemPrecioHistorial;
    }

    /// METODOS

    @Override
    public List<ItemPrecioHistorialDTO> getAllProducts() {
        return itemPrecioHistorialRepositorio.findAll()
                .stream()
                .map(this::convertirADto)
                .toList();
    }

    @Override
    public Optional<ItemPrecioHistorialDTO> getProductById(Long id) {
        return itemPrecioHistorialRepositorio.findById(id).map(this::convertirADto);
    }

    @Override
    public ItemPrecioHistorialDTO crateProduct(CreateItemPrecioHistorialDTO createItemPrecioHistorialDTO) {
        ItemPrecioHistorial itemPrecioHistorial=convertirAItemPrecioHistorial(createItemPrecioHistorialDTO);
        ItemPrecioHistorial itemPrecioHistorialRegistrado=itemPrecioHistorialRepositorio.save(itemPrecioHistorial);
        return convertirADto(itemPrecioHistorialRegistrado);
    }

    @Override
    public Optional<ItemPrecioHistorialDTO> updateProduct(Long id, UpdateItemPrecioHistorialDTO updateItemPrecioHistorialDTO) {
        return itemPrecioHistorialRepositorio.findById(id).map(itemPrecioHistorial -> {
            if(updateItemPrecioHistorialDTO.getItem_id() != null ){
                itemPrecioHistorial.setItem_id(updateItemPrecioHistorialDTO.getItem_id());
            }
            if (updateItemPrecioHistorialDTO.getPrecio_anterior() != null){
                itemPrecioHistorial.setPrecio_anterior(updateItemPrecioHistorialDTO.getPrecio_anterior());
            }
            if (updateItemPrecioHistorialDTO.getPrecio_nuevo() != null){
                itemPrecioHistorial.setPrecio_nuevo(updateItemPrecioHistorialDTO.getPrecio_nuevo());
            }
            if (updateItemPrecioHistorialDTO.getFecha_cambio() != null){
                itemPrecioHistorial.setFecha_cambio(updateItemPrecioHistorialDTO.getFecha_cambio());
            }
            ItemPrecioHistorial itemPrecioHistorial1=itemPrecioHistorialRepositorio.save(itemPrecioHistorial);
            return convertirADto(itemPrecioHistorial1);

        });
    }

    @Override
    public boolean deleteProduct(Long id) {
        if (itemPrecioHistorialRepositorio.existsById(id)){
            itemPrecioHistorialRepositorio.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public String listado() {
        StringBuilder s = new StringBuilder();
        getAllProducts().forEach(i -> s
                .append( i.getItem_id() + ". ")
                .append( i )
                .append(",\n"));
        return s.toString();
    }


}
