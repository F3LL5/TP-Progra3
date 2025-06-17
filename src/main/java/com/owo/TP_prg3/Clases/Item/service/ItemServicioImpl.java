package com.owo.TP_prg3.Clases.Item.service;

import com.owo.TP_prg3.Clases.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Clases.Excepciones.RecursoNoEncontradoException;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuesto;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuestoRepositorio;
import com.owo.TP_prg3.Clases.Item.dto.CreateItemDTO;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Item.dto.UpdateItemDTO;
import com.owo.TP_prg3.Clases.Item.modelo.Item;
import com.owo.TP_prg3.Clases.Item.modelo.ItemRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ItemServicioImpl implements ItemServicio {
    //Atributos
    @Autowired
    private ItemRepositorio itemRepositorio;
    @Autowired
    private InventarioPuestoRepositorio inventarioPuestoRepositorio;

    //Conversion
    private ItemDTO convertirA_DTO(Item item){
        return new ItemDTO(
                item.getItem_id(),
                item.getNombre(),
                item.getCategoria()
        );
    }

    private Item convertirA_Item(CreateItemDTO itemDTO){
        Item item = new Item();
        item.setNombre(itemDTO.getNombre());
        item.setCategoria(itemDTO.getCategoria());

        return item;
    }

    //Metodos
    @Override
    public List<ItemDTO> getAllProducts() {
        return itemRepositorio.findAll()
                        .stream()
                        .map(this::convertirA_DTO)
                        .toList();
    }

    @Override
    public Optional<ItemDTO> getProductById(Long id) {
        return Optional.of(itemRepositorio.findById(id)
                .map(this::convertirA_DTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Item con ID " + id + " no encontrado.")));
    }

    @Override
    public ItemDTO createProduct(CreateItemDTO createItemDTO) {
        Item item = convertirA_Item(createItemDTO);
        Item itemRegistrado = itemRepositorio.save(item);
        return convertirA_DTO(itemRegistrado);
    }

    @Override
    public String listado() {
        StringBuilder s = new StringBuilder();
        getAllProducts().forEach(i -> s
                .append(i.getItem_id() + ". ")
                .append(i)
                .append(",\n"));
        return s.toString();
    }

    @Override
    public Optional<ItemDTO> updateProduct(Long id, UpdateItemDTO updateItemDTO) {
        return itemRepositorio.findById(id)
                .map(item -> {
                    if (item == null) {
                        throw new RecursoNoEncontradoException("Item con ID " + id + " no encontrado para actualizar.");
                    }

                    if (updateItemDTO.getNombre() != null) {
                        item.setNombre(updateItemDTO.getNombre());
                    }
                    if (updateItemDTO.getCategoria() != null) {
                        item.setCategoria(updateItemDTO.getCategoria());
                    }
                    Item itemModificado = itemRepositorio.save(item);
                    return convertirA_DTO(itemModificado);
                })
                .or(() -> {
                    throw new RecursoNoEncontradoException("Item con ID " + id + " no encontrado para actualizar.");
                });
    }

    @Override
    public boolean deleteProduct(Long id) {
        if (itemRepositorio.existsById(id)) {
            itemRepositorio.deleteById(id);
            return true;
        }
        throw new RecursoNoEncontradoException("Item con ID " + id + " no encontrado para eliminar.");
    }

    public List<ItemDTO> filtrarYordenar(Long puestoId, String categoria, String orden, String direccion) {
        // 1. Obtener los IDs de los ítems que tienen stock (cantidad > 0)
        Stream<InventarioPuesto> inventarioStream = inventarioPuestoRepositorio.findAll().stream()
                .filter(inv -> inv.getCantidad() > 0); // Solo ítems con stock

        if (puestoId != null) {
            inventarioStream = inventarioStream.filter(inv -> inv.getPuesto().getPuestoId().equals(puestoId)); // Filtrar por puesto
        }

        // Obtener los IDs únicos de los ítems en stock y/o del puesto
        Set<Long> itemIdsInStock = inventarioStream
                .map(InventarioPuesto::getItemId)
                .collect(Collectors.toSet());

        // 2. Obtener los DTOs de los ítems correspondientes a esos IDs
        List<ItemDTO> itemsdto = itemRepositorio.findAllById(itemIdsInStock).stream()
                .map(this::convertirA_DTO)
                .toList();

        Stream<ItemDTO> itemstream = itemsdto.stream();

        // 3. Aplicar filtros de categoría
        if (categoria != null && !categoria.isEmpty()) {
            itemstream = itemstream.filter(item -> item.getCategoria().equalsIgnoreCase(categoria));
        }

        // 4. Aplicar ordenación
        if (orden != null) {
            Comparator<ItemDTO> comparador = null;
            switch (orden.toLowerCase()) {
                case "nombre" -> comparador = Comparator.comparing(ItemDTO::getNombre);
                case "categoria" -> comparador = Comparator.comparing(ItemDTO::getCategoria); // Añadida opción de ordenar por categoría
                default -> throw new IngresoInvalidoException("Opción de ordenación incorrecta: " + orden + ". Use 'nombre' o 'categoria'.");
            }

            if (comparador != null) {
                if ("desc".equalsIgnoreCase(direccion)) {
                    comparador = comparador.reversed();
                }
                itemstream = itemstream.sorted(comparador);
            }
        }
        return itemstream.toList();
    }

}
