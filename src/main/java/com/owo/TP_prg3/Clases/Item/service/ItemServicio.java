package com.owo.TP_prg3.Clases.Item.service;

import com.owo.TP_prg3.Clases.Item.dto.CreateItemDTO;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Item.dto.UpdateItemDTO;

import java.util.List;
import java.util.Optional;

public interface ItemServicio {
    List<ItemDTO> getAllProducts();
    Optional<ItemDTO> getProductById(Long id);
    ItemDTO createProduct(CreateItemDTO createItemDTO);
    Optional<ItemDTO> updateProduct(Long id, UpdateItemDTO updateItemDTO);
    boolean deleteProduct(Long id);
}
