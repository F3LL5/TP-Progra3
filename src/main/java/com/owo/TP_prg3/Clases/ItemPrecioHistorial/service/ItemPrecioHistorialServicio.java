package com.owo.TP_prg3.Clases.ItemPrecioHistorial.service;

import com.owo.TP_prg3.Clases.Item.dto.UpdateItemDTO;
import com.owo.TP_prg3.Clases.ItemPrecioHistorial.dto.CreateItemPrecioHistorialDTO;
import com.owo.TP_prg3.Clases.ItemPrecioHistorial.dto.ItemPrecioHistorialDTO;
import com.owo.TP_prg3.Clases.ItemPrecioHistorial.dto.UpdateItemPrecioHistorialDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ItemPrecioHistorialServicio {
    List<ItemPrecioHistorialDTO> getAllProducts();
    Optional<ItemPrecioHistorialDTO>getProductById(Long id);
    ItemPrecioHistorialDTO crateProduct(CreateItemPrecioHistorialDTO createItemPrecioHistorialDTO);
    Optional<ItemPrecioHistorialDTO> updateProduct(Long id, UpdateItemPrecioHistorialDTO updateItemPrecioHistorialDTO);
    boolean deleteProduct(Long id);
    String listado();
}
